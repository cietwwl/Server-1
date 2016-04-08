package com.rwbase.common.userEvent;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.Player;

public class UserEventMgr {

	private Map<UserEventType,IUserEventHandler> eventHandlerMap = new HashMap<UserEventType,IUserEventHandler>();
	
	public void raiseEvent(Player player, UserEvent userEvent){
		IUserEventHandler eventHandler = eventHandlerMap.get(userEvent.getEventType());
		if(eventHandler!=null){
			eventHandler.doEvent(player, userEvent.getParam());
		}
	}
	
	
}
