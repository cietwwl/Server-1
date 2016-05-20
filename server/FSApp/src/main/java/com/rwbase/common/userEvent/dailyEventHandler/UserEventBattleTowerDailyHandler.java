package com.rwbase.common.userEvent.dailyEventHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeSubItem;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventHandleTask;

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
				boolean isBetweendays = ActivityDailyCountTypeMgr
						.getInstance()
						.isOpen(ActivityDailyCountTypeSubCfgDAO
								.getInstance()
								.getById(
										ActivityDailyCountTypeEnum.BattleTowerDaily
												.getCfgId()));
				boolean isLevelEnough = ActivityDailyCountTypeMgr.getInstance().isLevelEnough(player);
				int addcount = 0;
				ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder
						.getInstance();
				ActivityDailyCountTypeItem dataItem = dataHolder.getItem(player
						.getUserId());
				
					ActivityDailyCountTypeSubItem subItem = ActivityDailyCountTypeMgr.getInstance().getbyDailyCountTypeEnum(player,
								ActivityDailyCountTypeEnum.BattleTowerDaily,dataItem);
				if(subItem != null){
					// 试练塔存在每日刷新，需要判断传入的最高层是否低于奖励表的最高层
					addcount = Integer.parseInt(params.toString()) - subItem.getCount();
				}else{
					addcount =  Integer.parseInt(params.toString());
				}
				if (isBetweendays&&isLevelEnough) {
					ActivityDailyCountTypeMgr.getInstance().addCount(player,
							ActivityDailyCountTypeEnum.BattleTowerDaily,
							addcount);

				}
			}

			@Override
			public void logError(Player player, Throwable ex) {
				StringBuilder reason = new StringBuilder(
						ActivityDailyCountTypeEnum.BattleTowerDaily.toString())
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