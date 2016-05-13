package com.rw.fsutil.log;

import org.apache.log4j.Logger;

public class SqlLog {

	
	private static Logger logger=Logger.getLogger("sqlLog");


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

	public static void error(String message){
		logger.error(message);
	}
}
