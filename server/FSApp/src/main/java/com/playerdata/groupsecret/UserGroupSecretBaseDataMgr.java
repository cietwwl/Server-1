package com.playerdata.groupsecret;

import com.rwbase.dao.groupsecret.pojo.UserGroupSecretDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;

/*
 * @author HC
 * @date 2016年5月26日 下午4:53:33
 * @Description 
 */
public class UserGroupSecretBaseDataMgr {
	private static UserGroupSecretBaseDataMgr mgr = new UserGroupSecretBaseDataMgr();

	public static UserGroupSecretBaseDataMgr getMgr() {
		return mgr;
	}

	private UserGroupSecretBaseDataMgr() {
	}

	/**
	 * 获取秘境的数据
	 * 
	 * @param userId
	 * @return
	 */
	public UserGroupSecretBaseData get(String userId) {
		return UserGroupSecretDataHolder.getHolder().get(userId);
	}

	/**
	 * 更新数据
	 * 
	 * @param userId
	 */
	public void update(String userId) {
		UserGroupSecretDataHolder.getHolder().updateData(userId);
	}
}