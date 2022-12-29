# /IInvolvedPartyIdentification_v3_RetrieveIdentityVerification

This API retrieves involved party's Identity check assessment information. This information is retrieved from CIS via the RATL adaptor. 
It accepts Involved Party Identifier as the input and will return a response containing transactionStatus, moreProcessing and ‘verifications’. 

This service is using latest log4jv2 logging framework.Log4jv2 xml config file can be found at below location:
https://github.com/nationwide-building-society/si-lib-log4j2-kafka-appender/tree/Log4j2Config

Service is using below GET opeation:
SOAP POST /IInvolvedPartyIdentification_v3_RetrieveIdentityVerification

Detailed information about this service can be found in the below page:

* [Confluence page](https://nbs-enterprise.atlassian.net/wiki/spaces/SISL/pages/5394989/nwsi-ace-retrieveidentityverification+3.0)

This service can be tested by both SoapUI and ReadyAPI. For Testing purpose below Stubs and TestSuite has been created:

STUB :
1. RATL_V1_STUB
2. AUDIT_V1_STUB

TestSuite :
1. nwsi-ace-retrieveidentityverification 3.0

Refer below link for more testing details :
https://nbsuk.sharepoint.com/sites/NBS_Eprise_Mware_DC_Middleware_Strategic/Shared%20Documents/Forms/Artefact%20View.aspx?RootFolder=%2Fsites%2FNBS_Eprise_Mware_DC_Middleware_Strategic%2FShared+Documents%2FArtefacts%2F5.+Core+Items%2FUnit+testing%2FTest+Framework+Documentation&OR=Teams-HL&CT=1650959254099&params=eyJBcHBOYW1lIjoiVGVhbXMtRGVza3RvcCIsIkFwcFZlcnNpb24iOiIyNy8yMjA0MDExMTQwOSJ9
