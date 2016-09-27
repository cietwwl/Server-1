package com.rw.dblog;

import org.apache.log4j.Logger;

public class DBLog {
	private static Logger dbLog = Logger.getLogger("dbLog");
	private static Logger errorLog = Logger.getLogger("errorLog");
	private static Logger sqlLog = Logger.getLogger("sqlLog");   //记录执行过的sql

	public static void LogInfo(String module, String message) {
		StringBuilder sb = new StringBuilder();
		sb.append(module).append("|").append(message);
		dbLog.info(sb.toString());
	}

	public static void LogError(String module, String message) {
		StringBuilder sb = new StringBuilder();
		sb.append(module).append("|").append(message);
		errorLog.info(sb.toString());
	}
	
	public static void LogSQL(String sql){
		sqlLog.info(sql);
	}
}
