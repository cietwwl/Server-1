package com.playerdata.groupsecret;

import com.rwbase.dao.groupsecret.pojo.UserGroupSecretDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;

/*
 * @author HC
 * @date 2016年5月26日 下午4:53:33
 * @Description 
 */
public class UserGroupSecretDataMgr {
	private static UserGroupSecretDataMgr mgr = new UserGroupSecretDataMgr();

	public static UserGroupSecretDataMgr getMgr() {
		return mgr;
	}

	private UserGroupSecretDataMgr() {
	}

	public UserGroupSecretBaseData get(String userId) {
		return UserGroupSecretDataHolder.getHolder().get(userId);
	}
}