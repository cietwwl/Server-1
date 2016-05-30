package com.log;

import org.apache.log4j.Logger;

import com.rwbase.gameworld.GameWorldFactory;




public class GameLog {

    private static Logger debugLog = Logger.getLogger("debugLog");
    
    private static Logger errorLog = Logger.getLogger("errorLog");
    
    
	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param message:操作内容，一般为动宾结构
	 * @param throwableP:捕获的异常，没有则传空
	 */
	public static void info(final String module,final String id,final String message,final Throwable throwableP){
		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {
			
			@Override
			public void run() {
				StringBuilder logContent = new StringBuilder();
				logContent.append(getStackTrace())
							.append(module).append("|")
							.append(id).append("|")
							.append(message).append("|");
				if(throwableP != null){
					debugLog.info(logContent, throwableP);
				}else{
					debugLog.info(logContent);
				}
			}
		});
	}

	/**
	 * logger info
	 * @param module	所属模块
	 * @param id		如果是系统则为system
	 * @param message	操作内容，一般为动宾结构
	 */
	public static void info(String module,String id,String message){
		info(module, id, message, null);
	}
	
	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param errorReason:失败原因
	 * @param throwableP:捕获的异常，没有则传空
	 */	
	public static void error(final LogModule module,final String id, final String errorReason, final Throwable throwableP){
		error(module.getName(), id, errorReason, throwableP);
	}
	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param errorReason:失败原因
	 * @param throwableP:捕获的异常，没有则传空
	 */	
	public static void error(final String module,final String id, final String errorReason, final Throwable throwableP){
		
		GameWorldFactory.getGameWorld().asynExecute(new Runnable() {

			@Override
			public void run() {
				
				StringBuilder logContent = new StringBuilder();
				logContent.append(getStackTrace())
				.append(module).append("|")
				.append(id).append("|")
				.append(errorReason).append("|");
				
				errorLog.error(logContent, throwableP);
			}
			
		});
	}
	
	/**
	 * 按模块和功能记录日志
	 * @param module
	 * @param id
	 * @param errorReason
	 */
	public static void error(String module,String id, String errorReason){
		error(module, id, errorReason, null);
	}
	
	private static String getStackTrace(){
		Throwable cause = new Throwable();
		StringBuilder detail = new StringBuilder("");
		StackTraceElement[] stackTrace = cause.getStackTrace();
		
		Object callMethod = stackTrace[stackTrace.length-2];
		detail.append(callMethod.toString());	
		return detail.toString();
	}
	

	/***** 错误日志****/
	@Deprecated
	public static void error(String message , Throwable throwableP ){		
		
		error("", "", message, throwableP);

	}
	
	/***** 错误日志****/
	@Deprecated
	public static void error(String message  ){		
		error("", "", message, null);
		
	}
	
	/***** 错误日志****/
	@Deprecated
	public static void error(Throwable throwableP ){		
		error("", "", "", throwableP);

	}
	
	
	/***** 调试 日志****/
	public static void debug(Object obj){
//		debugLog.info(getStackTrace() + obj);
	}
	
	
	
	
}
