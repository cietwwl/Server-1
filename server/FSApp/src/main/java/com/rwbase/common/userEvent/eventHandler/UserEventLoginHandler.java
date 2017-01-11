package com.rwbase.common.userEvent.eventHandler;

import java.util.ArrayList;
import java.util.List;

import com.bm.serverStatus.ServerStatusMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activityCommon.ActivityDetector;
import com.playerdata.activityCommon.activityType.ActivityTypeFactory;
import com.rwbase.common.userEvent.IUserEventHandler;

public class UserEventLoginHandler implements IUserEventHandler {

	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();

	public UserEventLoginHandler() {
		init();
	}

	private void init() {
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				
				ServerStatusMgr.processGmMailWhenLogin(player);
				
				/** 活动是否开启 */
				if(!ActivityDetector.getInstance().containsActivityByActId(ActivityTypeFactory.CountType, ActivityCountTypeEnum.Login.getCfgId())){
					return;
				}
				ActivityCountTypeMgr.getInstance().addCount(player, ActivityCountTypeEnum.Login, 1);
			}

			@Override
			public void logError(Player player, Exception ex) {
				StringBuilder reason = new StringBuilder(
						ActivityCountTypeEnum.Login.toString())
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
