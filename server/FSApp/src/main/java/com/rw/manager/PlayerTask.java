package com.rw.manager;

import com.playerdata.Player;

public interface PlayerTask {
	
	public void doCallBack(Player player);
	
	public String getName();
}
