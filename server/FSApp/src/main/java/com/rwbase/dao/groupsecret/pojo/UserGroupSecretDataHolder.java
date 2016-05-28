package com.rwbase.dao.groupsecret.pojo;

import com.rwbase.dao.groupsecret.pojo.db.UserGroupSecretBaseData;
import com.rwbase.dao.groupsecret.pojo.db.dao.UserGroupSecretBaseDataDAO;

/*
 * @author HC
 * @date 2016年5月26日 下午4:55:06
 * @Description 
 */
public class UserGroupSecretDataHolder {

	private static UserGroupSecretDataHolder holder = new UserGroupSecretDataHolder();

	public static UserGroupSecretDataHolder getHolder() {
		return holder;
	}

	private UserGroupSecretDataHolder() {
	}

	/**
	 * 获取角色对应秘境的数据
	 * 
	 * @return
	 */
	public UserGroupSecretBaseData get(String userId) {
		return UserGroupSecretBaseDataDAO.getDAO().get(userId);
	}

	/**
	 * 更新数据
	 * 
	 * @param userId
	 */
	public void updateData(String userId) {
		UserGroupSecretBaseDataDAO.getDAO().update(userId);
	}
}