@Library('sl-deploy-jenkins-operations')

systemVersion = '0.0.0'
artefactBuildInfo = null
deployment = [: ]
runScans = false;
runDeployment = false;
destroyDeployment = false;
jenkinsUserId = null;
jenkinsUser = null;
isEnvironmentRemoved = false;
deployDependencies = true;
def aceImageCompileVersion = "12.0.3.0-ACE-WITH-LAIT39377-LAIT39458"
def applicationName = "nwsi-ace-retrieveidentityverification-3.0"
def stubPorts = ["9095", "8091"]
def stubMessageStore = ""
def excelDataSource = ""
def readyApiProject = "nwsi-ace-retrieveidentityverification-3.0-readyapi-project.xml"
def soapUiProject = "nwsi-ace-retrieveidentityverification-3.0-soapui-project.xml"
def projectToRun = ""
def testRunnerDir = ""
def resultsXmlFile = ""
def mockServiceCreated = false
//Code coverage
def serviceAppLabel = "retrieve-identityverification"
def serviceBarPath = "/home/aceuser/ace-server/run/nwsi-ace-retrieveidentityverification-3.0/uk"
def targetTraceFile = "userTraceMerged.txt"
def targetEsqlFile = "esqlMerged.esql"
def aceLogFolder = "/home/aceuser/ace-server/config/common/log"
def codeCoverageComplete = false;
def codeCoverageResultFile = "codeCoverageResults.txt"
//Folder in repo holding the source file for compilation
def sourceDir = "nwsi-ace-retrieveidentityverification-3.0"
											   

isTimeTriggered = false;

skipStage = true; //Used to skip stages that aren't ready.

// Skip build if triggered due to Branch Indexing
println "Build cause is: " + currentBuild.getBuildCauses().toString()
if (currentBuild.getBuildCauses().toString().contains('BranchIndexingCause')) {
  print "INFO: Build skipped due to trigger being Branch Indexing"
  currentBuild.result = 'ABORTED'
  return
}

runPipeline = shouldRunPipeline()
pipeline {
  agent {
    label 'java-build-agent'
  }
  triggers {
    cron(env.BRANCH_NAME == 'master' ? '00 03 * * *' : '')
  }

  environment {
    systemName = 'retrieve-identityverification'
  }

  parameters {
    booleanParam(
      defaultValue: true,
      description: 'Destroy the CI deployment at the end of the job.',
      name: 'willDestroyDeployment'
    )
  booleanParam(
      defaultValue: true,
      description: 'Run Sonar scan on this branch.',
      name: 'runScan'
    )
    booleanParam(
      defaultValue: true,
      description: 'Publish the artefacts from this branch and run the tests',
      name: 'publishAndTest'
    )
    booleanParam(
      defaultValue: true,
      description: 'Use Soap UI? (If false, ReadyAPI will be deployed)',
      name: 'useSoapUI'
    )
  }

  stages {
    stage('Determine build type') {
      steps {
        script {
          mandatoryScanDeploy = env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith("release/") || env.BRANCH_NAME == "PR-$env.CHANGE_ID"
          runScans = params.runScan || mandatoryScanDeploy
          runDeployment = params.publishAndTest || mandatoryScanDeploy
          destroyDeployment = params.willDestroyDeployment
          echo "Run Scan value is $runScans"
          echo "Deploy value is $runDeployment"

          wrap([$class: 'BuildUser']) {

            echo "----------------------------------------------------------------------"
            echo "BUILD_USER that started this Pipeline: ${env.BUILD_USER}"
            echo "BUILD_USER_ID that started this Pipeline: ${env.BUILD_USER_ID}"
            echo "BUILD_USER_FIRST_NAME that started this Pipeline: ${env.BUILD_USER_FIRST_NAME}"
            echo "BUILD_USER_LAST_NAME that started this Pipeline: ${env.BUILD_USER_LAST_NAME}"
            echo "BUILD_USER_EMAIL that started this Pipeline: ${env.BUILD_USER_EMAIL}"
            echo "BUILD_USER that started this Pipeline: ${BUILD_USER}"
            echo "----------------------------------------------------------------------"

            jenkinsUserId = "${env.BUILD_USER_ID}"
            jenkinsUser = "${BUILD_USER}"
          }
          if (isTimedBuild() || !jenkinsUserId.contains("nationwide.co.uk") || !jenkinsUser.contains("nationwide.co.uk")) {
            destroyDeployment = true
          }
          echo "Will Destroy Deployment value is $destroyDeployment"
        }
      }
    }
    stage('Initialise ABI') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        script {
          artefactBuildInfo = artefactBuildInfoInitialise()
        }
      }
    }
    stage('Generate version') {
      steps {
        script {
          systemVersion = versionNumber()
        }
      }
    }

    stage('Scan') {
      when {
        expression { runScans }
      }
      failFast true
      parallel {
        stage('SonarQube scan') {
          steps {
            aceSonarScan(
              serviceProjectKey: applicationName,
              serviceProjectName: applicationName,
              serviceProjectBaseDir: sourceDir,
              serviceProjectVer: systemVersion
            )
          }
        }
      }
    }
    stage('Build') {
      steps {
          aceBuildBarImage(
           systemVersion   : systemVersion,
           applicationName :  applicationName,
           sourceDir: sourceDir
          )
      }
    }

    stage('Publish') {
      when {
        expression {
          //runDeployment && !skipStage
	    runDeployment
        }
      }
	  failFast true
      parallel {
        stage('Publish stage') {
		  steps {
		    echo "Publish"
			}
		}
	}
    }
    stage('Snyk scan') {
      when {
        expression { runDeployment }
            }
            failFast true
            parallel {
                stage('Snyk scan nwsi-ace-retrieveidentityverification-3.0') {
                    steps {
                        snykScanTBSImage(imageName: 'nwsi-ace-retrieveidentityverification-3.0', imageVersion: systemVersion, project: 'nwsi-ace-retrieveidentityverification-3.0')
                    }
                }
            }
    }
    stage ('Fetch Helm chart dependencies') {
        when {
            expression { runPipeline && runDeployment }
        }
        steps {
            helmUpdateDependencies(chartPath: systemName)
        }
    }
    stage('Validate Helm chart') {
        when {
						
            expression { runPipeline && runDeployment }
        }
        steps {
            helmLint(
                chartPath: systemName,
                withSubCharts: true
            )
            helmTemplate(
                chartPath: systemName
            )
        }
    }
	
    stage('Publish Helm chart') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'artifactory-user', usernameVariable: 'user', passwordVariable: 'key')]) {
          script {
            helmPackage(
              chartPath: systemName,
              systemVersion: systemVersion
            )
            helmDeploy(
              chartName: systemName,
              chartVersion: systemVersion,
              helmRepositoryUrl: "${env.SPEEDLAYER_HELM_REPOSITORY_URL}"
            )
          }
        }
      }
    }
    stage('Complete ABI') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        script {
          if (artefactBuildInfo != null) {
            def artefacts = [: ]
            artefacts[systemName] = [kind: "HelmChart", version: systemVersion, extensions: [: ]]
            artefacts['${applicationName}'] = [kind: "ContainerImage", version: systemVersion, extensions: [: ]]
          }
        }
      }
    }
    stage('Trigger Deployment') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        script {
          //Deploy a Kubernetes namespace and the ACE helm chart.
          deployAceEnvironment(
            [
              systemName: systemName,
              systemVersion: systemVersion,
              environmentProvisioningCloneUrl: "${env.ENVIRONMENT_PROVISIONING_REPOSITORY_CLONE_URL}",
              systemAppVersions: "${systemName}:${systemVersion}",
              deployDependencies: params.deployDependencies,
              mockServerName: "http://${NODE_NAME}.jenkins"
            ],
            deployment
          )
        }
      }
    }
    stage('Setup ReadyAPI / SoapUI') {
      steps {
        script {
          if (params.useSoapUI) {
            addSoapUI()
            projectToRun = soapUiProject
            testRunnerDir = "soapuiRunner"
            resultsXmlFile = "TEST-retrieve-identityverification.xml" //The SOAP UI report file varies based on the suite being run. Specify it here to ensure we can read it later
          } else {
            addSoapUI() //Needed as ReadyAPI doesn't have mockservicerunner.
            addReadyAPI()
            projectToRun = readyApiProject
            testRunnerDir = "testRunner"
            resultsXmlFile = "report.xml" //ReadyAPI always generates a report called "report.xml"
          }
        }
      }
    }
    stage('create Kubernetes service for stub') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        script {
          //Each stub runs on the Jenkins agent. The ACE deployment will need to be able to communicate with the stub(s)
          //so create a service on the Jenkins agent for each stub port.
          createKubernetesService(
            deploymentNamespace: "jenkins",
            podName: "${env.NODE_NAME}",
            ports: stubPorts
          )
        }
      }
    }
    stage('Start mock service') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        script {
          //Launch the Mock service stub. If multiple stubs are required, then rather than copy / paste the launchMockRunner
          //command, add the stub project names and stubMessageStore to (for example) a map (e.g. stubs[stubName]=stubmessageStore)
          //then loop through the map using the key and value for project name and message store.
          stubMessageStore_RATL="StubMessageStore=${env.WORKSPACE}/stubMessageStore/RATL_V1_STUB"
		  stubMessageStore_Audit = "StubMessageStore=${env.WORKSPACE}/stubMessageStore/AUDIT_V1_STUB"																																							
          echo "Stub message store: ${stubMessageStore_RATL}"
          launchMockRunner(
             project: "integration-tests/RATL-V1-STUB-soapui-project.xml",
             mockService: "RATL_V1_STUB",
             installFolder: "soapuiRunner",
             extraParams: "-P${stubMessageStore_RATL}"
           )
		  echo "Stub message store: ${stubMessageStore_Audit}"
          launchMockRunner(
            project: "integration-tests/AUDIT_V1_STUB_SOAPUI.xml",
            mockService: "AUDIT_V1_STUB",
            installFolder: "soapuiRunner",
            extraParams: "-P${stubMessageStore_Audit}"
          )
          mockServiceCreated = true;
          sleep 10;
        }
      }
    }
    stage('Setup code coverage') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        script {
          aceGetEsqlFiles(
            deploymentNamespace: deployment.namespace,
            serviceAppLabel: serviceAppLabel,
            serviceBarPath: serviceBarPath,
            targetEsqlFile: targetEsqlFile
          )
          setAceUserTrace(
            url: "http://${systemName}.${deployment.namespace}:7600",
            action: "start"
          )
        }
      }
    }
    stage('Integration Tests') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        script {
          //Run the readyapi / soapui tests. The same command is used for both
          stubMessageStore="StubMessageStore=${env.WORKSPACE}/stubMessageStore"
          echo "Stub message store: ${stubMessageStore}"
		  testlocation = "integration-tests/nwsi-ace-retrieveidentityverification-3.0"																													 
          launchTestRunner(
            project: projectToRun,
            integrationTestDirectory: "${testlocation}",
            workspaceDirectory: "${env.WORKSPACE}",
            installFolder: testRunnerDir,
            isReadyApi: !useSoapUI, //This needs tidying - the param should be useSoapUI and the launchTestRunner needs modifying to match.
            extraParams: "-h${systemName}.${deployment.namespace}:7843 -ehttps://${systemName}.${deployment.namespace}:7843/IInvolvedPartyIdentification_v3_RetrieveIdentityVerification -P${stubMessageStore} -PdatasourceEndpoint=https://${systemName}.${deployment.namespace}:7843/",
			allureResultsDirectory: 'allure-report'
        )
        }
      }
    }
    stage('Run code coverage') {
      when {
        expression {
          runDeployment
        }
      }
      steps {
        script {
          setAceUserTrace(
            url: "http://${systemName}.${deployment.namespace}:7600",
            action: "stop"
          )
          aceGetUserTraceFiles(
            deploymentNamespace: deployment.namespace,
            serviceAppLabel: serviceAppLabel,
            aceLogFolder: aceLogFolder,
            targetTraceFile: targetTraceFile
          )
          codeCoverageComplete = true
          aceRunCodeCoverageScript(
            esqlFile: targetEsqlFile,
            userTraceFile: targetTraceFile,
            codeCoverageResultFile: codeCoverageResultFile
          )
        }
      }
    }													
    stage('Update Build Audit') {
      when {
        expression {
          runDeployment && artefactBuildInfo != null && !skipStage
        }
      }
      steps {
        artefactBuildAuditInfoUpdate(artefactBuildInfo)
      }
    }
    stage('Destroy Environment') {
      when {
        expression {
          destroyDeployment && runDeployment
        }
      }
      steps {
        script {
          removeEnvironmentByName(
            environmentNamespace: deployment.namespace,
            deployDependencies: deployDependencies
          )
          isEnvironmentRemoved = true
        }
      }
    }
  }
  post {
    always {
      script {
        if (codeCoverageComplete) {
          archiveArtifacts artifacts: codeCoverageResultFile
        }
        allureReport(integrationTestDirectory: "./", surefireOutputDirectory: "allure-results")
        //This will loop through the XML results, use the resources/jiraTestCaseIdMapping file to get the Jira TC ID for eac test
        //then create a json file containing the JiraTC ID and the result and push this to Jira using the ZephyrScale plugin.
        //createReadyApiZephyrResultsFile(resultsXml: "allure-results/" + resultsXmlFile)
        if (runDeployment && !skipStage) {
          def list = [systemName]
          tbsDeleteImage(imageNames: list)
        }
      }
    }
    unsuccessful {
      script {
        def result = (!isEnvironmentRemoved).toString()

        // Final environment cleanup, for cases where environment was created and then the job failed / was aborted
        if (destroyDeployment && deployment.containsKey("namespace") && !isEnvironmentRemoved) {
          removeEnvironmentByName(
            environmentNamespace: deployment.namespace,
            deployDependencies: deployDependencies,
          )
          deleteKubernetesService(
                  deploymentNamespace: "jenkins",
                  serviceName: "${env.NODE_NAME}"
          )
        }
      }
      emailFailureReport()
    }
    cleanup {
      script {
        if (mockServiceCreated) {
          deleteKubernetesService(
            deploymentNamespace: "jenkins",
            serviceName: "${NODE_NAME}"
          )
        }
      }
      deleteDir() // clean up our workspace
    }
  }
}
