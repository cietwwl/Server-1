package com.rwbase.common.userEvent.dailyEventHandler;

import java.util.ArrayList;
import java.util.List;



import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;

import com.playerdata.activity.dailyCountType.ActivityDailyTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyTypeSubItem;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.UserEventHandleTask;

public class UserEventBattleTowerDailyHandler implements IUserEventHandler{

	
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	
	public UserEventBattleTowerDailyHandler(){
		init();	
	}
	
	private void init() {
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				/** 活动是否开启 */
				boolean isBetweendays = ActivityDailyTypeMgr
						.getInstance()
						.isOpen(ActivityDailyTypeSubCfgDAO
								.getInstance()
								.getById(
										ActivityDailyTypeEnum.BattleTowerDaily
												.getCfgId()));
				boolean isLevelEnough = ActivityDailyTypeMgr.getInstance().isLevelEnough(player);
				int addcount = 0;
				ActivityDailyTypeItemHolder dataHolder = ActivityDailyTypeItemHolder
						.getInstance();
				ActivityDailyTypeItem dataItem = dataHolder.getItem(player
						.getUserId());
				
					ActivityDailyTypeSubItem subItem = ActivityDailyTypeMgr.getInstance().getbyDailyCountTypeEnum(player,
								ActivityDailyTypeEnum.BattleTowerDaily,dataItem);
				if(subItem != null){
					// 试练塔存在每日刷新，需要判断传入的最高层是否低于奖励表的最高层
					addcount = Integer.parseInt(params.toString()) - subItem.getCount();
				}else{
					addcount =  Integer.parseInt(params.toString());
				}
				if (isBetweendays&&isLevelEnough) {
					ActivityDailyTypeMgr.getInstance().addCount(player,
							ActivityDailyTypeEnum.BattleTowerDaily,
							addcount);

				}
			}

			@Override
			public void logError(Player player, Throwable ex) {
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