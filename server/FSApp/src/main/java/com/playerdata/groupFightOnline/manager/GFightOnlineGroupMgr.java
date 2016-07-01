package com.playerdata.groupFightOnline.manager;

import com.playerdata.Player;

public class GFightOnlineGroupMgr {
	
	private static GFightOnlineGroupMgr instance = new GFightOnlineGroupMgr();
	
	public static GFightOnlineGroupMgr getInstance(){
		return instance;
	}
	
	public void synData(Player player, int version){
//		GFightOnlineGroupHolder.getInstance().synAllData(player, resourceID, version);
	}
}
