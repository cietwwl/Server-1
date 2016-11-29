package com.rwbase.common.timer;

public enum FSMinuteTaskType {

	//DEMO(com.rwbase.common.timer.test.FSGameMinuteTaskDemo.class, 1, false),
	;
	private Class<? extends IGameTimerTask> _classOfTask; // 實例化的class
	private int _intervalMinutes; // 執行的间隔（1~59）
	private boolean _isFixded; // 是否整分任务
	
	private FSMinuteTaskType(Class<? extends IGameTimerTask> pClassOfTask, int pIntervalMinutes,  boolean pIsFixed) {
		this._classOfTask = pClassOfTask;
		this._intervalMinutes = pIntervalMinutes;
		this._isFixded = pIsFixed;
	}
	
	public Class<? extends IGameTimerTask> getClassOfTask() {
		return _classOfTask;
	}
	
	public int getIntervalMinutes() {
		return _intervalMinutes;
	}
	
	public boolean isFixed() {
		return _isFixded;
	}
}
