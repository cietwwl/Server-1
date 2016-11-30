package com.bm.targetSell.param;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.bm.targetSell.TargetSellManager;
import com.rw.fsutil.util.DateUtils;
import com.rw.manager.ServerSwitch;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwbase.gameworld.GameWorld;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerPredecessor;

public class TargetSellSendTask implements IGameTimerTask {

	private static long roleCheckTime = TimeUnit.MINUTES.toMillis(15);
	private static long heroCheckTime = TimeUnit.MINUTES.toMillis(20);
	private static Logger remoteMsgLogger = Logger.getLogger("remoteMsgLogger");

	@Override
	public String getName() {
		return "TargetSellSendTask";
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		long current = System.currentTimeMillis();
		GameWorld gameWorld = GameWorldFactory.getGameWorld();
		for (Iterator<Entry<String, TargetSellRoleChange>> iterator = TargetSellManager.RoleAttrChangeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, TargetSellRoleChange> entry = iterator.next();
			TargetSellRoleChange value = entry.getValue();
			long startTime = value.getStartTime();
			if (current - value.getStartTime() >= roleCheckTime) {
				String userId = entry.getKey();
				remoteMsgLogger.error("over check time:" + userId + "," + DateUtils.getTimeOfDayFomrateTips(startTime) + "," + value.getChangeList());
				gameWorld.asyncExecute(userId, new PlayerPredecessor() {

					@Override
					public void run(String userId) {
						TargetSellManager.getInstance().checkAndPackHeroChanged(userId, true);
					}
				});
			}
		}
		for (Iterator<Entry<String, TargetSellHeroChange>> iterator = TargetSellManager.HeroAttrChangeMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, TargetSellHeroChange> entry = iterator.next();
			TargetSellHeroChange value = entry.getValue();
			long createTime = value.getCreateTime();
			if (current - createTime > heroCheckTime) {
				iterator.remove();
				remoteMsgLogger.error("over check time by hero:" + entry.getKey() + "," + DateUtils.getTimeOfDayFomrateTips(createTime) + "," + value.getChangeList());
			}
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
