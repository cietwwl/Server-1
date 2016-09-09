package com.playerdata.groupcompetition.util;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.groupcompetition.data.CompetitionStage;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class CompetitionStageEndCaller implements IGameTimerTask {

	private CompetitionStage _monitoringStage;
	
	public CompetitionStageEndCaller(CompetitionStage pMonitoringStage) {
		this._monitoringStage = pMonitoringStage;
	}
	
	@Override
	public String getName() {
		return "Monitoring Task for " + _monitoringStage;
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		_monitoringStage.onEnd();
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
		return Collections.emptyList();
	}

}
