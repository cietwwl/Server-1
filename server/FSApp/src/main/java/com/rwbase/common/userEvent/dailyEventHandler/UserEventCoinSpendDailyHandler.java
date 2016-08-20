package com.rwbase.common.userEvent.dailyEventHandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeSubCfgDAO;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventHandleTask;

public class UserEventCoinSpendDailyHandler implements IUserEventHandler{

	
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	
	public UserEventCoinSpendDailyHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
					/**活动是否开启*/
					boolean isBetweendays = ActivityDailyTypeMgr.getInstance().isOpen(ActivityDailyTypeSubCfgDAO
							.getInstance().getListByEnumId(ActivityDailyTypeEnum.CoinSpendDaily.getCfgId()));
					boolean isLevelEnough = ActivityDailyTypeMgr.getInstance().isLevelEnough(player);
					if(isBetweendays&&isLevelEnough){
						ActivityDailyTypeMgr.getInstance().addCount(player, ActivityDailyTypeEnum.CoinSpendDaily,Integer.parseInt(params.toString()));	
						
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityDailyTypeEnum.CoinSpendDaily.toString()).append(" error");				
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