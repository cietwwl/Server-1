package com.rwbase.common.userEvent.vitalityTypeEventHandler;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventHandleTask;

public class UserEventGivePowerVitalityHandler implements IUserEventHandler{
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	public UserEventGivePowerVitalityHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
//				boolean isBetween = ActivityDailyTypeMgr.getInstance().isOpen(ActivityDailyTypeSubCfgDAO
//						.getInstance().getById(ActivityDailyTypeEnum.GoldSpendDaily.getCfgId()));
//				boolean isLevelEnough = ActivityDailyTypeMgr.getInstance().isLevelEnough(player);
				ActivityVitalityTypeMgr.getInstance().addCount(player, ActivityVitalityTypeEnum.GivePowerVitality, Integer.parseInt(params.toString()));
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityVitalityTypeEnum.GivePowerVitality.toString()).append(" error");				
				GameLog.error(LogModule.ComActivityVitality, "userId:"+player.getUserId(), reason.toString(),ex);
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
