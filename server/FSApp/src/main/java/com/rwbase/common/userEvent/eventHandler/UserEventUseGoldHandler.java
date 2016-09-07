package com.rwbase.common.userEvent.eventHandler;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.rwbase.common.userEvent.IUserEventHandler;

public class UserEventUseGoldHandler implements IUserEventHandler {

	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();

	public UserEventUseGoldHandler() {
		init();
	}

	private void init() {
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				if (ActivityCountTypeCfgDAO.getInstance().isOpenAndLevelEnough(
						player.getLevel(), ActivityCountTypeEnum.GoldSpending)) {
					ActivityCountTypeMgr.getInstance().addCount(player,
							ActivityCountTypeEnum.GoldSpending,
							Integer.parseInt(params.toString()));
				}
			}

			@Override
			public void logError(Player player, Exception ex) {
				StringBuilder reason = new StringBuilder(
						ActivityCountTypeEnum.GoldSpending.toString())
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
