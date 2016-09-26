package com.rwbase.common.userEvent.dailyEventHandler;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventHandleTask;

public class UserEventUpGradeStarDailyHandler implements IUserEventHandler {

	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();

	public UserEventUpGradeStarDailyHandler() {
		init();
	}

	private void init() {
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				/** 活动是否开启 */
				ActivityDailyTypeSubCfgDAO instance = ActivityDailyTypeSubCfgDAO
						.getInstance();
				if (instance.isOpenAndLevelEnough(
						player.getLevel(),
						instance.getCfgMapByEnumid(ActivityDailyTypeEnum.UpGradeStarDaily
								.getCfgId()))) {
					ActivityDailyTypeMgr.getInstance().addCount(player,
							ActivityDailyTypeEnum.UpGradeStarDaily, 1);
				}
			}

			@Override
			public void logError(Player player, Exception ex) {
				StringBuilder reason = new StringBuilder(
						ActivityDailyTypeEnum.UpGradeStarDaily.toString())
						.append(" error");
				GameLog.error(LogModule.UserEvent,
						"userId:" + player.getUserId(), reason.toString(), ex);
			}
		});

	}

	@Override
	public void doEvent(Player player, Object params) {

		for (UserEventHandleTask userEventHandleTask : eventTaskList) {
			userEventHandleTask.doWrapAction(player, params);
		}

	}

}