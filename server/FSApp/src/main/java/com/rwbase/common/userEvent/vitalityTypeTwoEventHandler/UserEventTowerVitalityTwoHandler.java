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
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.UserEventHandleTask;

public class UserEventTowerVitalityTwoHandler implements IUserEventHandler{
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	public UserEventTowerVitalityTwoHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				ActivityVitalitySubCfg subCfg = ActivityVitalitySubCfgDAO.getInstance().getByTypeAndActiveType(ActivityVitalityTypeEnum.VitalityTwo,ActivityVitalityTypeEnum.TowerVitalityTwo.getCfgId());
				
				boolean isLevelEnough = ActivityVitalityTypeMgr.getInstance().isLevelEnough(ActivityVitalityTypeEnum.VitalityTwo,player);
				if(subCfg!=null&&isLevelEnough){
					ActivityVitalityTypeMgr.getInstance().addCount(player, ActivityVitalityTypeEnum.VitalityTwo,ActivityVitalityTypeEnum.TowerVitalityTwo,subCfg, Integer.parseInt(params.toString()));
					GameLog.error(LogModule.ComActivityVitality, "userId:"+player.getUserId(), "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~活动之王-送体开启",null);
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityVitalityTypeEnum.TowerVitalityTwo.toString()).append(" error");				
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
