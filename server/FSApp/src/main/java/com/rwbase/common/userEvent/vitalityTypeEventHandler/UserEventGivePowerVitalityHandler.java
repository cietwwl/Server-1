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
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.UserEventHandleTask;

public class UserEventGivePowerVitalityHandler implements IUserEventHandler{
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	public UserEventGivePowerVitalityHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				ActivityVitalitySubCfg subCfg = ActivityVitalitySubCfgDAO.getInstance().getByTypeAndActiveType(ActivityVitalityTypeEnum.Vitality,ActivityVitalityTypeEnum.GivePowerVitality.getCfgId());
				
				boolean isLevelEnough = ActivityVitalityTypeMgr.getInstance().isLevelEnough(ActivityVitalityTypeEnum.Vitality,player);
				if(subCfg!=null&&isLevelEnough){
					ActivityVitalityTypeMgr.getInstance().addCount(player, ActivityVitalityTypeEnum.GivePowerVitality,subCfg, Integer.parseInt(params.toString()));
					GameLog.error(LogModule.ComActivityVitality, "userId:"+player.getUserId(), "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~活动之王-送体开启",null);
					}
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
