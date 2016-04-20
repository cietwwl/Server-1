package com.rwbase.common.userEvent;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;
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
	
	
	public void RoleLogin(Player player, long lastLoginTime) {
		
		UserEvent userEvent = new UserEvent(UserEventType.LOGIN, lastLoginTime);
		raiseEvent(player, userEvent);
	}

	
	
	
	public void raiseEvent(Player player, UserEvent userEvent){
		IUserEventHandler eventHandler = eventHandlerMap.get(userEvent.getEventType());
		if(eventHandler!=null){
			eventHandler.doEvent(player, userEvent.getParam());
		}
	}
	
	
}
