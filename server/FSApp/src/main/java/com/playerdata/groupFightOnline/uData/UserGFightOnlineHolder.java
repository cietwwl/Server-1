package com.playerdata.groupFightOnline.uData;

import com.rwproto.DataSynProtos.eSynType;

public class UserGFightOnlineHolder {
	private static UserGFightOnlineHolder instance = new UserGFightOnlineHolder();
	private static UserGFightOnlineDAO gfPersonalDao = UserGFightOnlineDAO.getInstance();
	
	public static UserGFightOnlineHolder getInstance() {
		return instance;
	}

	private UserGFightOnlineHolder() { }
	final private eSynType synType = eSynType.GFightOnlinePersonalData;
}
