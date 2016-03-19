package com.rw.common;

import org.apache.log4j.Logger;

public class RobotLog {

	
	private static Logger logger=Logger.getLogger("robotLog");
	
	private static Logger failLog=Logger.getLogger("failLog");


	/***** 信息日志 ****/
	public static void info(String message) {	
		logger.info(message);
	}
	
	public static void fail(String message){
		failLog.info(message);
	}
	public static void fail(String message, Throwable throwableP){
		failLog.error(message, throwableP);
	}
	

}
