package com.playerdata.groupFightOnline.uData;

import com.rwproto.DataSynProtos.eSynType;

public class GFightOnlineResourceHolder {
	private static GFightOnlineResourceHolder instance = new GFightOnlineResourceHolder();
	private static GFightOnlineResourceDAO gfResourceDao = GFightOnlineResourceDAO.getInstance();

	public static GFightOnlineResourceHolder getInstance() {
		return instance;
	}

	private GFightOnlineResourceHolder() { }
	final private eSynType synType = eSynType.GFightOnlineResourceData;
}
