package com.bm.serverStatus;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class ProcessGmNoticeTimerTask implements IGameTimerTask{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		// TODO Auto-generated method stub
		ServerStatusMgr.processGmNotice();
		return null;
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rejected(RejectedExecutionException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isContinue() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		// TODO Auto-generated method stub
		return null;
	}

}
