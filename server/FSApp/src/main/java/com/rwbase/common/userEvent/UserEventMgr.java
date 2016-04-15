package com.rwbase.common.userEvent;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.eLog.eBILogType;
import com.rw.service.log.template.BILogTemplate;
import com.rw.service.log.template.ZoneRegLogTemplate;
import com.rwbase.common.userEvent.eventHandler.UserEventLoginHandler;

public class UserEventMgr {
	private static UserEventMgr instance = new UserEventMgr();
	
	
	private Map<UserEventType,IUserEventHandler> eventHandlerMap = new HashMap<UserEventType,IUserEventHandler>();
	
	public static UserEventMgr getInstance() {
		return instance;
	}
	
	private UserEventMgr(){
		eventHandlerMap.put(UserEventType.LOGIN, new UserEventLoginHandler());
		
		
	}
	
	
	public void logRoleLogin(Player player) {
		
		UserEvent userEvent = new UserEvent(UserEventType.LOGIN, null);
		raiseEvent(player, userEvent);
	}

	
	
	
	public void raiseEvent(Player player, UserEvent userEvent){
		IUserEventHandler eventHandler = eventHandlerMap.get(userEvent.getEventType());
		if(eventHandler!=null){
			eventHandler.doEvent(player, userEvent.getParam());
		}
	}
	
	
}
