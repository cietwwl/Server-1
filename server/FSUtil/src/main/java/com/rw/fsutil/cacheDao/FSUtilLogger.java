package com.rw.fsutil.cacheDao;

import org.apache.log4j.Logger;

public class FSUtilLogger {

	private static Logger fsutilLog = Logger.getLogger("fsutilLog");
	private static Logger infoLogger = Logger.getLogger("fsutilInfo");
	
	public static void error(String text) {
		fsutilLog.error(text);
	}

	public static void error(String text, Throwable t) {
		fsutilLog.error(text, t);
	}

	public static void warn(String text) {
		fsutilLog.warn(text);
	}

	public static void info(String text) {
		infoLogger.info(text);
	}
}
