package com.bm.rank.magicsecret;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class MSDailyRefreshMgr implements IGameTimerTask{

	@Override
	public String getName() {
		return "乾坤幻境每日排名奖励";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		MSScoreRankMgr.dispatchMSDailyReward(timeSignal.getAssumeExecuteTime());
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
