package com.common.serverdata;


public class ServerCommonDataHolder {
	private static ServerCommonDataHolder instance = new ServerCommonDataHolder();
	
	public static ServerCommonDataHolder getInstance() {
		return instance;
	}
	
	public ServerCommonData get() {
		return ServerCommonDataDAO.getInstance().get("1");
	}
	
	public void update(ServerCommonData data) {
		ServerCommonDataDAO.getInstance().update(data);
	}
}
