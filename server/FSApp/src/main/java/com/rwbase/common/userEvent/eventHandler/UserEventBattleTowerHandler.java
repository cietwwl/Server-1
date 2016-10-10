package com.rwbase.common.userEvent.eventHandler;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.playerdata.activity.countType.data.ActivityCountTypeItem;
import com.playerdata.activity.countType.data.ActivityCountTypeItemHolder;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.UserEventHandleTask;


public class UserEventBattleTowerHandler implements IUserEventHandler{

private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	
	public UserEventBattleTowerHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				/**活动是否开启*/
				boolean isBetweendays = ActivityCountTypeMgr.getInstance().isOpen(ActivityCountTypeCfgDAO.getInstance().getCfgById(ActivityCountTypeEnum.BattleTower.getCfgId()));
				boolean isLevelEnough = ActivityCountTypeMgr.getInstance().isLevelEnough(player,ActivityCountTypeCfgDAO.getInstance().getCfgById(ActivityCountTypeEnum.BattleTower.getCfgId()));
				ActivityCountTypeItemHolder dataHolder = ActivityCountTypeItemHolder.getInstance();
				
				ActivityCountTypeItem dataItem = dataHolder.getItem(player.getUserId(), ActivityCountTypeEnum.BattleTower);
				//试练塔存在每日刷新，需要判断传入的最高层是否低于奖励表的最高层
				if(dataItem == null){
					return;
				}
				int addcount = Integer.parseInt(params.toString()) - dataItem.getCount();
				if(addcount > 0&&isBetweendays&&isLevelEnough){
					ActivityCountTypeMgr.getInstance().addCount(player, ActivityCountTypeEnum.BattleTower,addcount);	
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityCountTypeEnum.BattleTower.toString()).append(" error");				
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
