package com.rwbase.common.timer;

import com.bm.rank.groupCompetition.groupRank.GroupFightRankRefreshMgr;
import com.playerdata.activity.growthFund.GrowthFundGlobalDataSaveTask;
import com.rwbase.common.timer.core.FSGameTimerDataSaver;

public enum FSMinuteTaskType {

	TIMER_DATA_SAVE_TASK(FSGameTimerDataSaver.class, 1, false),
	GROUP_FIGHT_RANK_REFRESH(GroupFightRankRefreshMgr.class, 15, false),
	GROWTH_FUND_SAVE_TASK(GrowthFundGlobalDataSaveTask.class, 5, false),
	//DEMO(com.rwbase.common.timer.test.FSGameMinuteTaskDemo.class, 1, false),
	;
	private Class<? extends IGameTimerTask> _classOfTask; // 實例化的class
	private int _intervalMinutes; // 執行的间隔（1~59）
	private boolean _isFixded; // 是否整分任务
	
	private FSMinuteTaskType(Class<? extends IGameTimerTask> pClassOfTask, int pIntervalMinutes,  boolean pIsFixed) {
		this._classOfTask = pClassOfTask;
		this._intervalMinutes = pIntervalMinutes;
		this._isFixded = pIsFixed;
	}
	
	public Class<? extends IGameTimerTask> getClassOfTask() {
		return _classOfTask;
	}
	
	public int getIntervalMinutes() {
		return _intervalMinutes;
	}
	
	public boolean isFixed() {
		return _isFixded;
	}
}
