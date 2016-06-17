package com.playerdata.groupFightOnline.uData;

import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.rw.fsutil.cacheDao.DataKVDao;

public class GFightOnlineGroupDAO extends DataKVDao<UserMagicSecretData>{
	private static GFightOnlineGroupDAO instance = new GFightOnlineGroupDAO();

	public static GFightOnlineGroupDAO getInstance() {
		return instance;
	}

	private GFightOnlineGroupDAO() {
	}
}
