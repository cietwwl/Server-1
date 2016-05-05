package com.fy;

import org.apache.log4j.Logger;




public class ChargeLog {

    
    private static Logger chargeLog = Logger.getLogger("chargeLog");
    
	public static void info(final String module,final String id,final String message){
		info(module, id, message, null);		
	}

	
	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param message:操作内容，一般为动宾结构
	 * @param throwableP:捕获的异常，没有则传空
	 */
	public static void info(final String module,final String id,final String message,final Throwable throwableP){
		StringBuilder logContent = new StringBuilder();
		logContent.append(getStackTrace())
					.append(module).append("|")
					.append(id).append("|")
					.append(message).append("|");
		chargeLog.info(logContent, throwableP);
		
	}

	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param errorReason:失败原因
	 * @param throwableP:捕获的异常，没有则传空
	 */	
	public static void error(final String module,final String id, final String errorReason, final Throwable throwableP){
		
		StringBuilder logContent = new StringBuilder();
		logContent.append(getStackTrace())
		.append(module).append("|")
		.append(id).append("|")
		.append(errorReason).append("|");
		
		chargeLog.error(logContent, throwableP);
	}
	

	
	private static String getStackTrace(){
		Throwable cause = new Throwable();
		StringBuilder detail = new StringBuilder("");
		StackTraceElement[] stackTrace = cause.getStackTrace();
		
		Object callMethod = stackTrace[stackTrace.length-2];
		detail.append(callMethod.toString());	
		return detail.toString();
	}
	

	
	
	/***** 调试 日志****/
	public static void debug(Object obj){
//		debugLog.info(getStackTrace() + obj);
	}
	
	
	
	
}
