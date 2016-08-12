package com.rwbase.common.timer.core;

import java.util.concurrent.TimeUnit;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.IGameTimerTaskSubmitInfo;

public class FSGameTimerTaskSubmitInfoImpl implements IGameTimerTaskSubmitInfo {
	
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

	private IGameTimerTask _task;
	private int _delay;
	private TimeUnit _unit;
	
	FSGameTimerTaskSubmitInfoImpl(IGameTimerTask pTask, int pDelay, TimeUnit pUnit) {
		this._task = pTask;
		this._delay = pDelay;
		this._unit = pUnit;
	}
	
	public IGameTimerTask getTask() {
		return _task;
	}

	public void setTask(IGameTimerTask pTask) {
		this._task = pTask;
	}

	public int getInterval() {
		return _delay;
	}

	public void setDelay(int pDelay) {
		this._delay = pDelay;
	}

	public TimeUnit getTimeUnitOfInterval() {
		return _unit;
	}

	public void setUnit(TimeUnit pUnit) {
		this._unit = pUnit;
	}
}
