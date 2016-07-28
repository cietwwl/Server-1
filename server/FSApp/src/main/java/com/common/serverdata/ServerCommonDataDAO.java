package com.common.serverdata;

import com.rw.fsutil.cacheDao.DataKVDao;

public class ServerCommonDataDAO extends DataKVDao<ServerCommonData>{
	private static ServerCommonDataDAO instance = new ServerCommonDataDAO();

	public static ServerCommonDataDAO getInstance() {
		return instance;
	}

	private ServerCommonDataDAO() { }
	
	public ServerCommonData get(String id){
		ServerCommonData data = super.get(id);
		if(data == null){
			data = new ServerCommonData();
			data.setId(id);
			data.teamBattleDailyReset();
			instance.update(data);
		}
		return data;
	}
}
