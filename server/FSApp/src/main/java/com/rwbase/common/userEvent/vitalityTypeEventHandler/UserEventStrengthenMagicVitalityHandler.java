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

public class UserEventStrengthenMagicVitalityHandler implements IUserEventHandler{
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	public UserEventStrengthenMagicVitalityHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
//				ActivityVitalitySubCfg subCfg = ActivityVitalitySubCfgDAO.getInstance().getByTypeAndActiveType(ActivityVitalityTypeEnum.Vitality,ActivityVitalityTypeEnum.StrengthenMagicVitality.getCfgId());
//				
//				boolean isLevelEnoughAndOpen = ActivityVitalityTypeMgr.getInstance().isLevelEnough(ActivityVitalityTypeEnum.Vitality,player);
//				if(subCfg!=null&&isLevelEnoughAndOpen){
//					ActivityVitalityItemHolder activityVitalityItemHolder = ActivityVitalityItemHolder.getInstance();
//					ActivityVitalityTypeItem activityVitalityTypeItem = activityVitalityItemHolder.getItem(player.getUserId(), ActivityVitalityTypeEnum.Vitality);
//					ActivityVitalityTypeSubItem activityVitalityTypeSubItem = activityVitalityTypeItem.getByType(subCfg.getType());
//					int add = Integer.parseInt(params.toString());
//					if(activityVitalityTypeSubItem.getCount() > 0){
//						add = add - activityVitalityTypeSubItem.getCount()>0?add - activityVitalityTypeSubItem.getCount() : 0;
//					}
//					ActivityVitalityTypeMgr.getInstance().addCount(player, ActivityVitalityTypeEnum.Vitality,subCfg, add);
//					}
				ActivityVitalityTypeMgr.getInstance().addCount(player, ActivityVitalityTypeEnum.StrengthenMagicVitality, Integer.parseInt(params.toString()));
			}
			
			@Override
			public void logError(Player player,Exception ex) {
				StringBuilder reason = new StringBuilder(ActivityVitalityTypeEnum.StrengthenMagicVitality.toString()).append(" error");				
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
