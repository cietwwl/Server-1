package com.rwbase.common.timer;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 日常任务枚举
 * 
 * @author CHEN.P
 *
 */
public enum FSDailyTaskType {

	//DEMO(999, com.rwbase.common.timer.test.FSGameDailyTaskDemo.class, 14, 30);
	;
	private static final Map<Class<? extends IGameTimerTask>, Integer> _typeOfClasses = new HashMap<Class<? extends IGameTimerTask>, Integer>();
	
	static {
		FSDailyTaskType[] all = values();
		for(FSDailyTaskType fdtt : all) {
			_typeOfClasses.put(fdtt._classOfTask, fdtt._type);
		}
	}
	private int _type; // 每日任務的類型，必須唯一
	private Class<? extends IGameTimerTask> _classOfTask; // 實例化的class
	private int _hourOfDay; // 執行的時間（24小時制）
	private int _minute; // 執行的分鐘

	private FSDailyTaskType(int pType, Class<? extends IGameTimerTask> pClassOfTask, int pHourOfDay, int pMinute) {
		this._type = pType;
		this._classOfTask = pClassOfTask;
		this._hourOfDay = pHourOfDay;
		this._minute = pMinute;
	}

	public int getType() {
		return _type;
	}

	public Class<? extends IGameTimerTask> getClassOfTask() {
		return _classOfTask;
	}

	public int getHourOfDay() {
		return _hourOfDay;
	}

	public int getMinute() {
		return _minute;
	}
	
	public static int getTypeByClass(Class<? extends IGameTimerTask> clazz) {
		Integer type = _typeOfClasses.get(clazz);
		if(type != null) {
			return type;
		}
		return 0;
	}
}
