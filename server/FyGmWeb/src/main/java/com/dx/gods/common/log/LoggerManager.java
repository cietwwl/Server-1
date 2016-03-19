package com.dx.gods.common.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.dx.gods.common.log.data.LoggerData;
import com.dx.gods.common.utils.UtilTools;

public class LoggerManager {
	
	/**
	 * 获取指定条数的日志
	 * @param logType
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public static List<LoggerData> readLog(int logType, int count)
			throws Exception {

		List<LoggerData> list = new ArrayList<LoggerData>();

		try {
			EnumLog enumLog = EnumLog.getEnumLog(logType);
			String path = enumLog.getPath();
			Calendar instance = Calendar.getInstance();
			while (list.size() < count) {
				long time = instance.getTimeInMillis();
				String logFilePath = getLogFilePath(enumLog, time);
				File readFile = readFile(logFilePath);
				if (readFile == null) {
					break;
				}
				BufferedReader reader = new BufferedReader(new FileReader(readFile));
				String str = null;
				while ((str = reader.readLine()) != null) {
					LoggerData data = new LoggerData();
					data.parse(str);
					list.add(data);
				}
				instance = UtilTools.getLastDay(instance);
			}
		} catch (Exception ex) {
			throw new Exception("读取指定条数的log出错" + ex.getMessage());
		}
		return list;
	}

	/**
	 * 获取指定日期之间的日志 
	 * 最小间隔为天
	 * @param logType
	 * @param startTime
	 * @param endTime
	 * @param count
	 * @return
	 */
	public static List<LoggerData> readLog(int logType, long startTime, long endTime, int count)throws Exception {
		List<LoggerData> list = new ArrayList<LoggerData>();
		
		try {
			EnumLog enumLog = EnumLog.getEnumLog(logType);
			String path = enumLog.getPath();
			Calendar instance = Calendar.getInstance();
			instance.setTimeInMillis(endTime);
			int limited = count == -1 ? Integer.MAX_VALUE : count;
			while (list.size() < limited) {
				long time = instance.getTimeInMillis();
				if(time < startTime){
					break;
				}
				String logFilePath = getLogFilePath(enumLog, time);
				File readFile = readFile(logFilePath);
				if (readFile == null) {
					break;
				}
				BufferedReader reader = new BufferedReader(new FileReader(readFile));
				String str = null;
				while ((str = reader.readLine()) != null) {
					LoggerData data = new LoggerData();
					data.parse(str);
					list.add(data);
				}
				instance = UtilTools.getLastDay(instance);
			}
		} catch (Exception ex) {
			throw new Exception("读取指定日期的log出错" + ex.getMessage());
		}
		return list;
	}

	private static File readFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}

	private static String getLogFilePath(EnumLog enumLog, long time) {
		if (UtilTools.isCurrentDay(time)) {
			return enumLog.getPath() + "/" + enumLog.getLogName();
		} else {
			String strDate = UtilTools.getDateTimeString(time, "yyyyMMdd");
			return enumLog.getPath() + "/" + enumLog.getLogName() + "_"
					+ strDate + ".log";
		}
	}
}
