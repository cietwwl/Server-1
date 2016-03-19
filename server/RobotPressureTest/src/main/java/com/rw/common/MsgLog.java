package com.rw.common;

import org.apache.log4j.Logger;

public class MsgLog {

	
	private static Logger logger=Logger.getLogger("msgLog");


	/***** 信息日志 ****/
	public static void info(String message) {	
		
		logger.info(message);
	}
	
	/***** 错误日志 ****/
	public static void error(String message, Throwable throwableP) {
		
		throwableP.printStackTrace();
		logger.error(message, throwableP);
	}
	
	/***** 错误日志 ****/
	public static void error(Throwable throwableP) {
		throwableP.printStackTrace();
		logger.error(throwableP);
	}

}
