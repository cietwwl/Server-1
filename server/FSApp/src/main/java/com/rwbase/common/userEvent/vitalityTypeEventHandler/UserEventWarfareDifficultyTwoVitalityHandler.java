package com.rwbase.common.userEvent.vitalityTypeEventHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeEnum;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalityCfgDAO;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfg;
import com.playerdata.activity.VitalityType.cfg.ActivityVitalitySubCfgDAO;
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
				
				boolean isLevelEnough = ActivityVitalityTypeMgr.getInstance().isLevelEnough(ActivityVitalityTypeEnum.Vitality,player);
				int[] ints = (int[])params;
				if(ints == null){
					return;
				}
				if(ints.length != 2){
					return;
				}
				if(ints[1]!=time||ints[0] != levelId){
					return;
				}
				if(subCfg!=null&&isLevelEnough){					
					ActivityVitalityTypeMgr.getInstance().addCount(player, ActivityVitalityTypeEnum.WarfareDifficultyTwoVitality,subCfg, 1);
					GameLog.error(LogModule.ComActivityVitality, "userId:"+player.getUserId(), "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~活动之王-送体开启",null);
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityVitalityTypeEnum.WarfareDifficultyTwoVitality.toString()).append(" error");				
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
