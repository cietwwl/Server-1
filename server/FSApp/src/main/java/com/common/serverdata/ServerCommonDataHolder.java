package com.common.serverdata;


public class ServerCommonDataHolder {
	private static ServerCommonDataHolder instance = new ServerCommonDataHolder();
	private ServerCommonData commonData = ServerCommonDataDAO.getInstance().get("1");
	
	public static ServerCommonDataHolder getInstance() {
		return instance;
	}
	
	public ServerCommonData get() {
		if(null == commonData)
			return ServerCommonDataDAO.getInstance().get("1");
		return commonData;
	}
	
	public synchronized void update(ServerCommonData data) {
		ServerCommonDataDAO.getInstance().update(data);
	}
}
