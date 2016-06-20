package com.playerdata.groupFightOnline.uData;

public class GFightOnlineGroupHolder {
	private static GFightOnlineGroupHolder instance = new GFightOnlineGroupHolder();
	private static GFightOnlineGroupDAO gfGroupDao = GFightOnlineGroupDAO.getInstance();
	
	public static GFightOnlineGroupHolder getInstance() {
		return instance;
	}

	private GFightOnlineGroupHolder() { }

	public void get(){
		
	}
}
