package com.playerdata.teambattle.manager;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class TBDailyRefreshMgr implements IGameTimerTask{

	@Override
	public String getName() {
		return "组队战的每日刷新";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		TBTeamItemMgr.getInstance().dailyReset(timeSignal.getAssumeExecuteTime());
		return "";
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
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		// TODO Auto-generated method stub
		return null;
	}
}
