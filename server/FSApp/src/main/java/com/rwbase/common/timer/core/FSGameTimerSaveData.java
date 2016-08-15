package com.rwbase.common.timer.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.rw.fsutil.util.jackson.JsonUtil;

public class FSGameTimerSaveData {
	
	private static final FSGameTimerSaveData _INSTANCE = new FSGameTimerSaveData(); // 單例

	private static final String _KEY_LAST_SERVER_SHUTDOWN_TIME_MILLIS = "1";
	private static final String _KEY_LAST_EXECUTE_TIME_OF_DAILY_TASK = "2";
	
	@JsonProperty(_KEY_LAST_SERVER_SHUTDOWN_TIME_MILLIS)
	private long _lastServerShutdownTimeMillis; // 最後一次停服時間
	
	@JsonProperty(_KEY_LAST_EXECUTE_TIME_OF_DAILY_TASK)
	private Map<Integer, Long> _lastExecuteTimeOfDailyTask = new HashMap<Integer, Long>(); // 每日任務最後一次執行的時間
	
	public static FSGameTimerSaveData getInstance() {
		return _INSTANCE;
	}
	
	protected FSGameTimerSaveData() {}
	
	static void parseData(String attribute) throws Exception {
		FSGameTimerSaveData tempData = JsonUtil.readValue(attribute, FSGameTimerSaveData.class);
		Field[] allFields = FSGameTimerSaveData.class.getDeclaredFields();
		for (int i = 0; i < allFields.length; i++) {
			Field tempField = allFields[i];
			if (tempField.isAnnotationPresent(JsonProperty.class)) {
				Object value = tempField.get(tempData);
				tempField.set(_INSTANCE, value);
			}
		}
	}
	
	/**
	 * 
	 * 獲取最後一次的停服時間
	 * 
	 * @return
	 */
	@JsonIgnore
	public long getLastServerShutdownTimeMillis() {
		return _lastServerShutdownTimeMillis;
	}
	
	/**
	 * 
	 * 獲取每日任務的最後一次執行的時間
	 * 
	 * @param type
	 * @return
	 */
	public long getLastExecuteTime(int type) {
		Long time = _lastExecuteTimeOfDailyTask.get(type);
		if(time == null) {
			return 0;
		}
		return time;
	}
	
	/**
	 * 
	 * 設置停服時間
	 * 
	 * @param time
	 */
	@JsonIgnore
	public void setServerShutdownTime(long time) {
		this._lastServerShutdownTimeMillis = time;
	}
	
	/**
	 * 
	 * 更新任務的最後一次執行時間
	 * 
	 * @param type
	 * @param timeMillis
	 */
	public void updateLastExecuteTimeOfTask(int type, long timeMillis) {
		this._lastExecuteTimeOfDailyTask.put(type, timeMillis);
	}
}
