package com.rw.service.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.rw.fsutil.json.JSONException;
import com.rw.fsutil.json.JSONObject;
import com.rw.fsutil.logger.LoggerQueue;
import com.rw.manager.GameManager;
import com.rw.service.log.eLog.eBILogType;

public class LogService {
	
	public final static HashMap<eBILogType, Class<?>> LogTypeMap = new HashMap<eBILogType, Class<?>>();
	private static LogService instance = new LogService();
	
	private LoggerQueue queue;
	
	public static LogService getInstance(){
		if(instance == null){
			instance = new LogService();
		}
		return instance;
	}
	
	public void initLogService(){
		queue = new LoggerQueue("logger_record", "dataSourceMT", GameManager.getLogServerIp(), GameManager.getLogServerPort(), 20000);
		
		
		for (Iterator<Entry<eBILogType, Class<?>>> iterator = LogTypeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<eBILogType, Class<?>> next = iterator.next();
			
		}
	}
	
	/**
	 * 获取指定类型log
	 * @param type
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public ILog getLogByType(int type) throws InstantiationException,
			IllegalAccessException {
		eBILogType logType = eBILogType.getLogType(type);
		Class<?> className = LogTypeMap.get(logType);
		ILog log = (ILog) className.newInstance();
		return log;
	}
	
	/**
	 * 添加log到发送列表
	 * @param log
	 */
	public void sendLog(ILog log){
		queue.addLogger(log.logToString());
	}
	/**
	 * 添加log到发送列表
	 * @param log
	 */
	public void sendLog(String log){
		queue.addLogger(log);
	}
	
	public String parseJson(JSONObject json, String sign) throws JSONException{
		String value = json.getString(sign);
		if(value.equals("null")){
			return "";
		}
		return value;
	}
}
