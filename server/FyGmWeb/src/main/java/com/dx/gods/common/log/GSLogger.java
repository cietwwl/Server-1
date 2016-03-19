package com.dx.gods.common.log;

import org.apache.log4j.Logger;

public class GSLogger {
	
	private static Logger logger = Logger.getLogger("gsLogger");
	
	public GSLogger(){
		
	}
	
	public static void recordOperatorLog(String userName, String operatorResult){
		logger.info("userName:" + userName + ", operator time:"+System.currentTimeMillis()+", operator result:" + operatorResult);
	}
	
	public static Logger getLogger(){
		return logger;
	}
	
}
