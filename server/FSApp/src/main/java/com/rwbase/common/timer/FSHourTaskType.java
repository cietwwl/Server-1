package com.rwbase.common.timer;

public enum FSHourTaskType {

	//DEMO(com.rwbase.common.timer.test.FSGameHourTaskDemo.class, 15, false),
	;
	private Class<? extends IGameTimerTask> _classOfTask; // 實例化的class
	private int _intervalHours; // 執行的间隔（1~23）
	private boolean _isFixed; // 是否整点任务
	
	private FSHourTaskType(Class<? extends IGameTimerTask> pClassOfTask, int pIntervalHour, boolean pIsFixed) {
		this._classOfTask = pClassOfTask;
		this._intervalHours = pIntervalHour;
	}
	
	public Class<? extends IGameTimerTask> getClassOfTask() {
		return _classOfTask;
	}
	
	public int getIntervalHours() {
		return _intervalHours;
	}
	
	public boolean isFixed() {
		return _isFixed;
	}
}
