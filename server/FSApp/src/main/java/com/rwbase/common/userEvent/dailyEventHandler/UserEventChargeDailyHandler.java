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
import com.rwbase.common.userEvent.UserEventHandleTask;

public class UserEventChargeDailyHandler implements IUserEventHandler{

	
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	
	public UserEventChargeDailyHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
					/**活动是否开启*/
					boolean isBetweendays = ActivityDailyTypeMgr.getInstance().isOpen(ActivityDailyTypeSubCfgDAO
							.getInstance().getById(ActivityDailyTypeEnum.ChargeDaily.getCfgId()));
					boolean isLevelEnough = ActivityDailyTypeMgr.getInstance().isLevelEnough(player);
					if(isBetweendays&&isLevelEnough){
						ActivityDailyTypeMgr.getInstance().addCount(player, ActivityDailyTypeEnum.ChargeDaily,Integer.parseInt(params.toString()));	
						
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityDailyTypeEnum.ChargeDaily.toString()).append(" error");				
				GameLog.error(LogModule.UserEvent, "userId:"+player.getUserId(), reason.toString(),ex);
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