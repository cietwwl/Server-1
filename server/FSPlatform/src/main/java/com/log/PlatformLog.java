package com.log;

import org.apache.log4j.Logger;

import com.rw.common.SynTask;
import com.rw.common.SynTaskExecutor;




public class PlatformLog {

    private static Logger debugLog = Logger.getLogger("debugLog");
    
    private static Logger warningLog = Logger.getLogger("warningLog");
    
    private static Logger errorLog = Logger.getLogger("errorLog");
    

	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param message:操作内容，一般为动宾结构
	 * @param throwableP:捕获的异常，没有则传空
	 */
	public static void info(final String module,final String id,final String message, final Throwable throwableP){
		
		
		
		SynTaskExecutor.submitTask(new SynTask() {
			
			@Override
			public void dotask() {
				StringBuilder logContent = new StringBuilder();
				logContent.append(getStackTrace())
							.append(module).append("|")
							.append(id).append("|")
							.append(message).append("|");
				
				
				
				debugLog.error(logContent, throwableP);
			}
		});
	}
	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param message:操作内容，一般为动宾结构
	 * @param throwableP:捕获的异常，没有则传空
	 */
	public static void info(final String module,final String id,final String message){
		
		
		
		SynTaskExecutor.submitTask(new SynTask() {
			
			@Override
			public void dotask() {
				StringBuilder logContent = new StringBuilder();
				logContent.append(getStackTrace())
				.append(module).append("|")
				.append(id).append("|")
				.append(message).append("|");
				
				
				
				debugLog.info(logContent);
			}
		});
	}

	
	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param errorReason:失败原因
	 * @param throwableP:捕获的异常，没有则传空
	 */	
	public static void error(final String module,final String id, final String errorReason, final Throwable throwableP){
		SynTaskExecutor.submitTask(new SynTask() {
			
			@Override
			public void dotask() {
				StringBuilder logContent = new StringBuilder();
				logContent.append(getStackTrace())
							.append(module).append("|")
							.append(id).append("|")
							.append(errorReason).append("|");
			
				
				errorLog.error(logContent, throwableP);
			}
		});

	}
	/***
	 * 
	 * @param module:所属模块
	 * @param id:发起者ID，如果是系统则为system
	 * @param errorReason:失败原因
	 * @param throwableP:捕获的异常，没有则传空
	 */	
	public static void error(final String module,final String id, final String errorReason){
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
		errorLog.error(message, throwableP);		

	}
	
	/***** 错误日志****/
	@Deprecated
	public static void error(String message  ){		
		errorLog.error(message);		
		
	}
	
	/***** 错误日志****/
	@Deprecated
	public static void error(Throwable throwableP ){		
		errorLog.error("", throwableP);		

	}
	
	
	/***** 调试 日志****/
	@Deprecated
	public static void debug(Object obj){
		debugLog.info(getStackTrace() + obj);
	}
	
	/***** 警告 日志****/
	public static void warning(Object obj){			
		warningLog.info(getStackTrace() + obj);
	}
	
	
	
}
