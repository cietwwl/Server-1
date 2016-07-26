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

public class UserEventArenaDailyHandler implements IUserEventHandler{

	
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	
	public UserEventArenaDailyHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
					/**活动是否开启*/
					boolean isBetweendays = ActivityDailyTypeMgr.getInstance().isOpen(ActivityDailyTypeSubCfgDAO
							.getInstance().getById(ActivityDailyTypeEnum.ArenaDaily.getCfgId()));
					boolean isLevelEnough = ActivityDailyTypeMgr.getInstance().isLevelEnough(player);
//					ActivityDailyCountTypeItemHolder dataHolder = ActivityDailyCountTypeItemHolder.getInstance();					
//					ActivityDailyCountTypeItem dataItem = dataHolder.getItem(player.getUserId());
//					ActivityDailyCountTypeSubItem subItem = ActivityDailyCountTypeMgr
//							.getInstance().getbyDailyCountTypeEnum(player,
//									ActivityDailyCountTypeEnum.ArenaDaily,
//									dataItem);
//					
					//胜利或失败获得的积分不一致。
					int addcount = Integer.parseInt(params.toString());					
					
					if(addcount>0&&isBetweendays&&isLevelEnough){
						ActivityDailyTypeMgr.getInstance().addCount(player, ActivityDailyTypeEnum.ArenaDaily,addcount);	
						
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityDailyTypeEnum.ArenaDaily.toString()).append(" error");				
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