package com.rwbase.common.userEvent.redEnvelopeTypeEventHandler;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;

import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeEnum;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfg;
import com.playerdata.activity.redEnvelopeType.cfg.ActivityRedEnvelopeTypeCfgDAO;
import com.rwbase.common.userEvent.IUserEventHandler;
import com.rwbase.common.userEvent.UserEventHandleTask;

public class UserEventGoldSpendRedEnvelopeHandler  implements IUserEventHandler{
	private List<UserEventHandleTask> eventTaskList = new ArrayList<UserEventHandleTask>();
	public UserEventGoldSpendRedEnvelopeHandler(){
		init();	
	}
	
	private void init(){
		eventTaskList.add(new UserEventHandleTask() {
			@Override
			public void doAction(Player player, Object params) {
				ActivityRedEnvelopeTypeCfg Cfg = ActivityRedEnvelopeTypeCfgDAO.getInstance().getCfgById(ActivityRedEnvelopeTypeEnum.redEnvelope.getCfgId());
				boolean isBetween = ActivityRedEnvelopeTypeMgr.getInstance().isOpen(Cfg);
				if(isBetween){
					ActivityRedEnvelopeTypeMgr.getInstance().addCount(player, Integer.parseInt(params.toString()));

					}				
				}
			@Override
			public void logError(Player player,Throwable ex) {
				StringBuilder reason = new StringBuilder(ActivityRedEnvelopeTypeEnum.redEnvelope.toString()).append(" error");				
				GameLog.error(LogModule.ComActivityRedEnvelope, "userId:"+player.getUserId(), reason.toString(),ex);
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
