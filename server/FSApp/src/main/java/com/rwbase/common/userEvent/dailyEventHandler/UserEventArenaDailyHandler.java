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
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeCfgDAO;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyCountTypeSubCfgDAO;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItem;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeItemHolder;
import com.playerdata.activity.dailyCountType.data.ActivityDailyCountTypeSubItem;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.eventHandler.UserEventHandleTask;

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
					boolean isBetweendays = ActivityDailyCountTypeMgr.getInstance().isOpen(ActivityDailyCountTypeSubCfgDAO
							.getInstance().getById(ActivityDailyCountTypeEnum.ArenaDaily.getCfgId()));
					boolean isLevelEnough = ActivityDailyCountTypeMgr.getInstance().isLevelEnough(player);
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
						ActivityDailyCountTypeMgr.getInstance().addCount(player, ActivityDailyCountTypeEnum.ArenaDaily,addcount);	
						
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityDailyCountTypeEnum.ArenaDaily.toString()).append(" error");				
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