package com.rwbase.common.timer.core;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.IGameTimerTask;

public class FSGameTimerDataSaver implements IGameTimerTask {

	@Override
	public String getName() {
		return "FSGameTimerDataSaver";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		try {
			FSGameTimerMgr.getTimerInstance().saveTimerGlobalData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "SUCCESS";
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		
	}

	@Override
	public boolean isContinue() {
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}

}
