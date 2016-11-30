package com.rwbase.common.userEvent.eventHandler;

import com.playerdata.Player;


public abstract class UserEventHandleTask {

	public void doWrapAction(Player player, Object params){
		try {
			doAction(player, params);
		} catch (Exception e) {
			logError(player,e);
		}
	}
	
	public abstract void doAction(Player player, Object params);
	
	public abstract void logError(Player player,Exception ex);
	
}
