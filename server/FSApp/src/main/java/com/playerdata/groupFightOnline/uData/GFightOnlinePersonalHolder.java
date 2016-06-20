package com.playerdata.groupFightOnline.uData;

public class GFightOnlinePersonalHolder {
	private static GFightOnlinePersonalHolder instance = new GFightOnlinePersonalHolder();
	private static GFightOnlinePersonalDAO gfPersonalDao = GFightOnlinePersonalDAO.getInstance();
	
	public static GFightOnlinePersonalHolder getInstance() {
		return instance;
	}

	private GFightOnlinePersonalHolder() { }
	
}
