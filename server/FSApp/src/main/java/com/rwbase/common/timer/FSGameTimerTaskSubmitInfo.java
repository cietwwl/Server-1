package com.rwbase.common.timer;

import java.util.concurrent.TimeUnit;

public class FSGameTimerTaskSubmitInfo {
	
	/**
	 * 任务类型：秒任务
	 */
	public static final int TASK_TYPE_SECOND = TimeUnit.SECONDS.ordinal();
	/**
	 * 任务类型：分钟任务
	 */
	public static final int TASK_TYPE_MINUTE = TimeUnit.MINUTES.ordinal();
	/**
	 * 任务类型：小时任务
	 */
	public static final int TASK_TYPE_HOUR = TimeUnit.HOURS.ordinal();

	private FSGameTimerTask _task;
	private int _delay;
	private TimeUnit _unit;
	
	private FSGameTimerTaskSubmitInfo(FSGameTimerTask pTask, int pDelay, TimeUnit pUnit) {
		this._task = pTask;
		this._delay = pDelay;
		this._unit = pUnit;
	}
	
	public static FSGameTimerTaskSubmitInfo createSecondTaskSubmitInfo(FSGameTimerTask pTask, int pDelay) {
		return new FSGameTimerTaskSubmitInfo(pTask, pDelay, TimeUnit.SECONDS);
	}
	
	public static FSGameTimerTaskSubmitInfo createMinuteTaskSubmitInfo(FSGameTimerTask pTask, int pDelay) {
		return new FSGameTimerTaskSubmitInfo(pTask, pDelay, TimeUnit.MINUTES);
	}
	
	public static FSGameTimerTaskSubmitInfo createHourTaskSubmitInfo(FSGameTimerTask pTask, int pDelay) {
		return new FSGameTimerTaskSubmitInfo(pTask, pDelay, TimeUnit.HOURS);
	}
	
	public FSGameTimerTask getTask() {
		return _task;
	}

	public void setTask(FSGameTimerTask pTask) {
		this._task = pTask;
	}

	public int getDelay() {
		return _delay;
	}

	public void setDelay(int pDelay) {
		this._delay = pDelay;
	}

	public TimeUnit getUnit() {
		return _unit;
	}

	public void setUnit(TimeUnit pUnit) {
		this._unit = pUnit;
	}
}
