package com.playerdata.groupsecret;

import com.rwbase.dao.groupsecret.pojo.UserCreateGroupSecretDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;

/*
 * @author HC
 * @date 2016年5月26日 下午10:02:23
 * @Description 
 */
public class UserCreateGroupSecretDataMgr {
	private static UserCreateGroupSecretDataMgr mgr = new UserCreateGroupSecretDataMgr();

	public static UserCreateGroupSecretDataMgr getMgr() {
		return mgr;
	}

	UserCreateGroupSecretDataMgr() {
	}

	/**
	 * 获取秘境数据
	 * 
	 * @param id
	 * @return
	 */
	public UserCreateGroupSecretData get(String userId) {
		return UserCreateGroupSecretDataHolder.getHolder().get(userId);
	}

	/**
	 * 刷新秘境的数据
	 * 
	 * @param userId
	 */
	public void updateData(String userId) {
		UserCreateGroupSecretDataHolder.getHolder().updateData(userId);
	}
}