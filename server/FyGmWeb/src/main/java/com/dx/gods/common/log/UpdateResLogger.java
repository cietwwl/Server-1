package com.dx.gods.common.log;

import org.apache.log4j.Logger;

public class UpdateResLogger {
	private UpdateResLogger(){}
	
	private static Logger logger = Logger.getLogger("updateresLogger");
	
	public static void info(String content){
		if(logger.isInfoEnabled()){
			logger.info(content);
		}
	}
	
	public static void error(String content){
		logger.error(content);
	}
	
	public static void recordOperatorLog(String userName, String operatorResult){
		logger.info("userName:" + userName + ", operator time:"
				+ System.currentTimeMillis() + ", operator result:"
				+ operatorResult);
	}
}
