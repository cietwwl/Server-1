package com.rwbase.dao.groupsecret.pojo;

import com.rwbase.dao.groupsecret.pojo.db.UserCreateGroupSecretData;
import com.rwbase.dao.groupsecret.pojo.db.dao.UserCreateGroupSecretDataDAO;

/*
 * @author HC
 * @date 2016年5月26日 下午5:40:55
 * @Description 
 */
public class UserCreateGroupSecretDataHolder {
	private static UserCreateGroupSecretDataHolder holder = new UserCreateGroupSecretDataHolder();

	public static UserCreateGroupSecretDataHolder getHolder() {
		return holder;
	}

	UserCreateGroupSecretDataHolder() {
	}

	/**
	 * 获取秘境的数据
	 * 
	 * @param id
	 * @return
	 */
	public UserCreateGroupSecretData get(String userId) {
		return UserCreateGroupSecretDataDAO.getDAO().get(userId);
	}

	/**
	 * 刷新秘境的数据
	 * 
	 * @param userId
	 */
	public void updateData(String userId) {
		UserCreateGroupSecretDataDAO.getDAO().update(userId);
	}
}