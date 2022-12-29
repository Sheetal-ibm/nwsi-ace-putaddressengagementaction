package uk.co.nationwide.nem.esb.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingUtility extends Object /*MbJavaComputeNode*/ {

	public static Long getLogLevel (String applicationName) 
	{
		Logger logger = LogManager.getLogger(applicationName);
		return new Long(logger.getLevel().intLevel());
	}
	
	public static void logging(String applicationName, String content)
	{
		Logger logger = LogManager.getLogger(applicationName);
		logger.log(logger.getLevel(), content);
	}
	
}
