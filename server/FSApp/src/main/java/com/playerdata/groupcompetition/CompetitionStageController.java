package com.playerdata.groupcompetition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.groupcompetition.data.CompetitionStage;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

/**
 * 
 * 帮派争霸阶段控制器，负责切换不同的阶段
 * 
 * @author CHEN.P
 *
 */
public class CompetitionStageController implements IGameTimerTask {

	private List<CompetitionStage> _stageQueue = new ArrayList<CompetitionStage>(); // 阶段队列
	
	/**
	 * 
	 * 第一阶段开始的时间
	 * 
	 * @param firstStageStartTime
	 */
	public void start(long firstStageStartTime) {
		long delay = System.currentTimeMillis() - firstStageStartTime;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		return null;
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
