package com.playerdata.groupFightOnline.manager;

import com.playerdata.Player;
import com.playerdata.groupFightOnline.data.GFightOnlineResourceHolder;

public class GFightOnlineResourceMgr {
	
	private static GFightOnlineResourceMgr instance = new GFightOnlineResourceMgr();
	
	public static GFightOnlineResourceMgr getInstance(){
		return instance;
	}
	
	public void synData(Player player, int version){
		GFightOnlineResourceHolder.getInstance().synData(player);
	}
	

}
