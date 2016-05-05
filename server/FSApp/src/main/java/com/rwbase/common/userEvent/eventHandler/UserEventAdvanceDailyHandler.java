package com.rwbase.common.userEvent.eventHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfgDAO;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.userEvent.IUserEventHandler;

public class UserEventAdvanceDailyHandler implements IUserEventHandler{

	
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	
	public UserEventAdvanceDailyHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
					/**活动是否开启*/
					boolean isBetweendays = ActivityDailyCountTypeMgr.getInstance().isOpen(ActivityDailyCountTypeCfgDAO.getInstance().getCfgById(ActivityDailyCountTypeEnum.LoginDaily.getCfgId()));

					if(isBetweendays){
						ActivityDailyCountTypeMgr.getInstance().addCount(player, ActivityDailyCountTypeEnum.LoginDaily,1);	
						
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityDailyCountTypeEnum.LoginDaily.toString()).append(" error");				
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