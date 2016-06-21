package com.playerdata.groupFightOnline.data;

import com.playerdata.Player;

public class GFightOnlineResourceHolder {
	private static GFightOnlineResourceHolder instance = new GFightOnlineResourceHolder();
	private static GFightOnlineResourceDAO gfResourceDao = GFightOnlineResourceDAO.getInstance();

	public static GFightOnlineResourceHolder getInstance() {
		return instance;
	}

	private GFightOnlineResourceHolder() { }
	// final private eSynType synType = eSynType.GFightOnlineResourceData;
	
	public GFightOnlineResourceData get(String resourceID) {
		return gfResourceDao.get(resourceID);
	}
	
	public void update(Player player, GFightOnlineResourceData data) {
		gfResourceDao.update(data);
	}
}
