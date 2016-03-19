package com.rw.service.log;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.json.JSONException;
import com.rw.fsutil.json.JSONObject;
import com.rw.fsutil.logger.LoggerQueue;
import com.rw.platform.PlatformFactory;
import com.rw.platform.Server;
import com.rw.service.log.eLog.eLogType;
import com.rw.service.log.infoPojo.ClientInfo;

public class LogService {
	
	public final static HashMap<eLogType, Class<?>> LogTypeMap = new HashMap<eLogType, Class<?>>();
	private static LogService instance = new LogService();
	
	private LoggerQueue queue;
	
	public static LogService getInstance(){
		if(instance == null){
			instance = new LogService();
		}
		return instance;
	}
	
	public void initLogService(){
		System.out.println("start init log service......................");
		queue = new LoggerQueue("logger_record", "dataSourcePF", PlatformFactory.getLogServerIp(), PlatformFactory.getLogServerPort(), 20000);
		LogTypeMap.put(eLogType.RegLog, RegLog.class);
		for (Iterator<Entry<eLogType, Class<?>>> iterator = LogTypeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<eLogType, Class<?>> next = iterator.next();
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
		eLogType logType = eLogType.getLogType(type);
		Class<?> className = LogTypeMap.get(logType);
		ILog log = (ILog) className.newInstance();
		return log;
	}
	
	/**
	 * 添加log到发送列表
	 * @param log
	 */
	public void sendLog(String log){
		queue.addLogger(log);
	}
	
	/**
	 * 添加log到发送列表
	 * @param log
	 */
	public void sendLog(ILog log, ClientInfo clientInfo){
		queue.addLogger(log.logToString(clientInfo));
	}
	
	public String parseJson(JSONObject json, String sign) throws JSONException{
		String value = json.getString(sign);
		if(value.equals("null")){
			return "";
		}
		return value;
	}
}
