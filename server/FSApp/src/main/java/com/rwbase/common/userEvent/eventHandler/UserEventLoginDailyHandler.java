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
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.userEvent.IUserEventHandler;

public class UserEventLoginDailyHandler implements IUserEventHandler{

	
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	
	public UserEventLoginDailyHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
					/**活动是否开启*/
					boolean isBetweendays = ActivityCountTypeMgr.getInstance().isOpen(ActivityCountTypeCfgDAO.getInstance().getCfgById(ActivityCountTypeEnum.LoginDaily.getCfgId()));
					/**登陆是否隔天;如果不加between则必须保证dataitem会在结束时立刻移出*/
					boolean isnewday = false;
					if(StringUtils.equals(params+"","0")){//没有活动的登陆数据，首次登陆
						isnewday = true;
					}else{
						if(!isnewday){					
							isnewday = DateUtils.dayChanged(Long.parseLong(params.toString()));
						}
					}
					
					GameLog.error("userlogindaily", "", DateUtils.getDateTimeFormatString(Long.parseLong(params.toString()), "yyyy-MM-dd HH:mm") + " 现在" + DateUtils.getDateTimeFormatString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm")  + " isnew" + isnewday + "  isbetw=" + isBetweendays);
					if(isnewday){
						ActivityCountTypeMgr.getInstance().refreshDateFreshActivity(player);//每日福利任务在此触发刷新
						if(isBetweendays){//每日福利任务的登陆类型子任务在此触发
							ActivityCountTypeMgr.getInstance().addCount(player, ActivityCountTypeEnum.LoginDaily,1);
						}
					}
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityCountTypeEnum.LoginDaily.toString()).append(" error");				
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
