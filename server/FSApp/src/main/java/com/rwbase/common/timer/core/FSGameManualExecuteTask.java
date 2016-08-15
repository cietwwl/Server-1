package com.rwbase.common.timer.core;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.FSDailyTaskType;
import com.rwbase.common.timer.IGameTimerTask;

public class FSGameManualExecuteTask implements IGameTimerTask {

	private int _executeTimes;
	private IGameTimerTask _targetTask;
	private long _assumeStartTime;
	
	public FSGameManualExecuteTask(int pExecuteTime, IGameTimerTask pTargetTask, long pAssumeStartTime) {
		this._executeTimes = pExecuteTime;
		this._targetTask = pTargetTask;
		this._assumeStartTime = pAssumeStartTime;
	}
	
	@Override
	public String getName() {
		return "每日任务起服后执行";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		if(_executeTimes > 0) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(_assumeStartTime);
			for(int i = 0; i < _executeTimes; i++) {
				timeSignal.updateAssumeTime(c.getTimeInMillis());
				_targetTask.onTimeSignal(timeSignal);
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
			int type = FSDailyTaskType.getTypeByClass(_targetTask.getClass());
			if(type > 0) {
				FSGameTimerSaveData.getInstance().updateLastExecuteTimeOfDailyTask(type, System.currentTimeMillis());
			}
		}
		return "DONE";
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		
	}

	@Override
	public boolean isContinue() {
		return false;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}

}
