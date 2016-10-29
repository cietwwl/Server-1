package com.playerdata.activity.growthFund;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

public class GrowthFundGlobalDataSaveTask implements IGameTimerTask {

	@Override
	public String getName() {
		return "GrowthFundGlobalDataSaveTask";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		GrowthFundGlobalData data = ActivityGrowthFundMgr.getInstance().getGlobalData();
		if (data.isDirty().compareAndSet(true, false)) {
			GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROWTH_FUND, JsonUtil.writeValue(data));
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
