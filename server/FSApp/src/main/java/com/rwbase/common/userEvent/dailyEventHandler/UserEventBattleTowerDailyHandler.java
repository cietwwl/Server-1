package com.rwbase.common.userEvent.dailyEventHandler;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventHandleTask;

public class UserEventBattleTowerDailyHandler implements IUserEventHandler {

	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();

	public UserEventBattleTowerDailyHandler() {
		init();
	}

	private void init() {
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				int addcount = 0;
				ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder.getInstance();
				List<ActivityDailyTypeItem> dataItems = dataHolder.getItemList(player.getUserId());
				for(ActivityDailyTypeItem dataItem : dataItems){
					ActivityDailyTypeSubItem subItem = ActivityDailyTypeMgr
							.getInstance().getByDailyCountTypeEnum(player,
									ActivityDailyTypeEnum.BattleTowerDaily,
									dataItem);
					if (subItem != null) {
						// 试练塔存在每日刷新，需要判断传入的最高层是否低于奖励表的最高层
						addcount = Integer.parseInt(params.toString())
								- subItem.getCount();
					} else {
						addcount = Integer.parseInt(params.toString());
					}
					ActivityDailyTypeMgr.getInstance().addCount(player,
							ActivityDailyTypeEnum.BattleTowerDaily, addcount);
				}
			}

			@Override
			public void logError(Player player, Exception ex) {
				StringBuilder reason = new StringBuilder(
						ActivityDailyTypeEnum.BattleTowerDaily.toString())
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