package com.rwbase.common.timer.test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimer;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class FSGameMinuteTaskDemo implements IGameTimerTask {

	@Override
	public String getName() {
		return "FSGameMinuteTaskDemo";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		System.out.println("FSGameMinuteTaskDemo#onTimeSignal : nowTime : " + FSGameTimer.FORMAT_DEBUG.format(new Date(System.currentTimeMillis())));
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
		return false;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}

}
