package com.rwbase.common.userEvent.vitalityTypeTwoEventHandler;

import java.util.ArrayList;
import java.util.List;


import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfgDAO;
import com.playerdata.activity.VitalityType.data.ActivityVitalityItemHolder;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeItem;
import com.playerdata.activity.VitalityType.data.ActivityVitalityTypeSubItem;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.UserEventHandleTask;

public class UserEventWarfareDifficultyTwoVitalityTwoHandler implements IUserEventHandler{
	
	public static final int levelId = 150041;//难度1
	public static final int time = 15;//通关
	
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	public UserEventWarfareDifficultyTwoVitalityTwoHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				ActivityVitalitySubCfg subCfg = ActivityVitalitySubCfgDAO.getInstance().getByTypeAndActiveType(ActivityVitalityTypeEnum.VitalityTwo,ActivityVitalityTypeEnum.WarfareDifficultyTwoVitalityTwo.getCfgId());
				
				boolean isLevelEnough = ActivityVitalityTypeMgr.getInstance().isLevelEnough(ActivityVitalityTypeEnum.VitalityTwo,player);
				int[] ints = (int[])params;
				if(ints == null){
					return;
				}
				if(ints.length != 2){
					return;
				}
				if(ints[0] != levelId){
					return;
				}
				if(subCfg!=null&&isLevelEnough){		
					ActivityVitalityItemHolder activityVitalityItemHolder = ActivityVitalityItemHolder.getInstance();
					ActivityVitalityTypeItem activityVitalityTypeItem = activityVitalityItemHolder.getItem(player.getUserId(), ActivityVitalityTypeEnum.VitalityTwo);
					ActivityVitalityTypeSubItem activityVitalityTypeSubItem = activityVitalityTypeItem.getByType(subCfg.getType());
					int add = ints[1];
					if(activityVitalityTypeSubItem.getCount() > 0){
						add = add - activityVitalityTypeSubItem.getCount()>0?add - activityVitalityTypeSubItem.getCount() : 0;
					}
					ActivityVitalityTypeMgr.getInstance().addCountTwo(player, ActivityVitalityTypeEnum.WarfareDifficultyTwoVitalityTwo,subCfg, add);
					GameLog.error(LogModule.ComActivityVitality, "userId:"+player.getUserId(), "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~活动之王-送体开启",null);
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityVitalityTypeEnum.WarfareDifficultyTwoVitalityTwo.toString()).append(" error");				
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
