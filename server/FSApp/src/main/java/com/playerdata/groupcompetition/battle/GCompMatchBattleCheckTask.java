package com.playerdata.groupcompetition.battle;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.groupcompetition.holder.GCompMatchDataHolder;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

/**
 * @Author HC
 * @date 2016年9月27日 下午4:31:52
 * @desc 启动一个三秒的时效去检查已经完成的匹配队列掉线，机器人战斗，以及超时
 **/

class GCompMatchBattleCheckTask implements IGameTimerTask {
	private static final String matchBattleTaskName = "匹配战斗时效";

	private volatile boolean isRun;// 设置时效

	/**
	 * 时效恢复
	 */
	void taskResume() {
		isRun = true;
	}

	/**
	 * 时效暂停
	 */
	void taskPause() {
		isRun = false;
	}

	@Override
	public String getName() {
		return matchBattleTaskName;
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		if (isRun) {
			GCompMatchDataHolder.getHolder().checkAllMatchBattleState();
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