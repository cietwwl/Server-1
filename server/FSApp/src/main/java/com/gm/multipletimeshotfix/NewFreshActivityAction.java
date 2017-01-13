package com.gm.multipletimeshotfix;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.optimize.DataValueAction;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class NewFreshActivityAction implements IGameTimerTask {

	private static volatile boolean run = true;
	private DataValueAction<Player> action = new NewFreshActivityActionTask();

	@Override
	public String getName() {
		return "NEW_CHECK_FRESH_ACT";// 检查开服活动
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		if (!run) {
			return "SUCCESS";
		}

		PlayerMgr.getInstance().execteOnlineOperation(action);
		FSUtilLogger.info("执行角色开服活动检查的时效进来执行了");
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
		return run;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}
}