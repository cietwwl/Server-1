package com.rwbase.common.userEvent.vitalityTypeEventHandler;

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
import com.rwbase.common.userEvent.eventHandler.UserEventHandleTask;

public class UserEventWarfareDifficultyTwoVitalityHandler implements IUserEventHandler{
	
	public static final int levelId = 150041;//难度1
	public static final int time = 15;//通关
	
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	public UserEventWarfareDifficultyTwoVitalityHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				ActivityVitalitySubCfg subCfg = ActivityVitalitySubCfgDAO.getInstance().getByTypeAndActiveType(ActivityVitalityTypeEnum.Vitality,ActivityVitalityTypeEnum.WarfareDifficultyTwoVitality.getCfgId());
				
				boolean isLevelEnoughAndOpen = ActivityVitalityTypeMgr.getInstance().isLevelEnough(ActivityVitalityTypeEnum.Vitality,player);
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
				if(subCfg!=null&&isLevelEnoughAndOpen){	
					ActivityVitalityItemHolder activityVitalityItemHolder = ActivityVitalityItemHolder.getInstance();
					ActivityVitalityTypeItem activityVitalityTypeItem = activityVitalityItemHolder.getItem(player.getUserId(), ActivityVitalityTypeEnum.Vitality);
					ActivityVitalityTypeSubItem activityVitalityTypeSubItem = activityVitalityTypeItem.getByType(subCfg.getType());
					int add = ints[1];
					if(activityVitalityTypeSubItem.getCount() > 0){
						add = add - activityVitalityTypeSubItem.getCount()>0?add - activityVitalityTypeSubItem.getCount() : 0;
					}
					
					ActivityVitalityTypeMgr.getInstance().addCount(player, ActivityVitalityTypeEnum.Vitality,subCfg, add);
					}
				}
			@Override
			public void logError(Player player,Exception ex) {
				StringBuilder reason = new StringBuilder(ActivityVitalityTypeEnum.WarfareDifficultyTwoVitality.toString()).append(" error");				
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
