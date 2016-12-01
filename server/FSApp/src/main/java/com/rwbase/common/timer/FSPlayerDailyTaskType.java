package com.rwbase.common.timer;

public enum FSPlayerDailyTaskType {

//	ALL_PLAYER_DEMO(997, com.rwbase.common.timer.test.FSGameAllPlayerOperableDemo.class, 22, 00),
//	EXCEPTION_DEMO(998, com.rwbase.common.timer.test.FSGamePlayerNullPointerDemo.class, 22, 00),
//	DEMO(999, com.rwbase.common.timer.test.FSGamePlayerOperableDemo.class, 22, 00);
	;
	private int _type; // 每日任務的類型，必須唯一
	private Class<? extends IPlayerOperable> _classOfTask; // 實例化的class
	private int _hourOfDay; // 執行的時間（24小時制）
	private int _minute; // 執行的分鐘

	private FSPlayerDailyTaskType(int pType, Class<? extends IPlayerOperable> pClassOfTask, int pHourOfDay, int pMinute) {
		this._type = pType;
		this._classOfTask = pClassOfTask;
		this._hourOfDay = pHourOfDay;
		this._minute = pMinute;
	}

	public int getType() {
		return _type;
	}

	public Class<? extends IPlayerOperable> getClassOfTask() {
		return _classOfTask;
	}

	public int getHourOfDay() {
		return _hourOfDay;
	}

	public int getMinute() {
		return _minute;
	}
}
