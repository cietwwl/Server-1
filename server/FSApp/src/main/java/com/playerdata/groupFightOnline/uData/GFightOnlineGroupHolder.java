package com.playerdata.groupFightOnline.uData;

import com.rwproto.DataSynProtos.eSynType;

public class GFightOnlineGroupHolder {
	private static GFightOnlineGroupHolder instance = new GFightOnlineGroupHolder();
	private static GFightOnlineGroupDAO gfGroupDao = GFightOnlineGroupDAO.getInstance();
	
	public static GFightOnlineGroupHolder getInstance() {
		return instance;
	}

	private GFightOnlineGroupHolder() { }

	final private eSynType synType = eSynType.GFightOnlineGroupData;
	
	public void get(){
		
	}
}
