package com.playerdata.groupcompetition.battle;

import com.playerdata.groupcompetition.util.GCompEventsStatus;
import com.rwbase.common.timer.core.FSGameTimerMgr;

/**
 * @Author HC
 * @date 2016年10月10日 下午3:08:17
 * @desc
 **/

public class EventsStatusForBattleCenter {

	private static EventsStatusForBattleCenter center = new EventsStatusForBattleCenter();

	public static EventsStatusForBattleCenter getInstance() {
		return center;
	}

	private GCompMatchBattleCheckTask matchTask = new GCompMatchBattleCheckTask();

	EventsStatusForBattleCenter() {
	}

	/**
	 * 提交一个时效任务
	 */
	public void start() {
		FSGameTimerMgr.getInstance().submitSecondTask(matchTask, 3);
	}

	/**
	 * 当阶段发生了改变
	 * 
	 * @param status
	 */
	public void onEventsStatusChange(GCompEventsStatus status) {
		EventsStatusForBattle strategy = EventsStatusForBattle.getByType(status);
		if (strategy != null) {
			strategy.handler(matchTask);
		}
	}
}