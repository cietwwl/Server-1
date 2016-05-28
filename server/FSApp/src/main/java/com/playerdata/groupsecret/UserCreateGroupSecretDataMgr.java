package com.playerdata.groupsecret;

import com.rwbase.dao.groupsecret.pojo.UserCreateGroupSecretDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;
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

	/**
	 * 增加秘境
	 * 
	 * @param userId
	 * @param secretData
	 */
	public void addGroupSecretData(String userId, GroupSecretData secretData) {
		UserCreateGroupSecretData userCreateGroupSecretData = get(userId);
		if (userCreateGroupSecretData == null) {
			return;
		}

		userCreateGroupSecretData.addGroupSecretData(secretData);
		updateData(userId);
	}

	/**
	 * 移除某个索引位置上的阵容信息
	 * 
	 * @param userId
	 * @param index
	 * @param id
	 */
	public void removeDefendInfoData(String userId, int index, int id) {
		UserCreateGroupSecretData userCreateGroupSecretData = get(userId);
		if (userCreateGroupSecretData == null) {
			return;
		}

		GroupSecretData groupSecretData = userCreateGroupSecretData.getGroupSecretData(id);
		if (groupSecretData == null) {
			return;
		}

		groupSecretData.removeDefendUserInfoData(index);
		if (groupSecretData.getDefendMap().isEmpty()) {
			userCreateGroupSecretData.deleteGroupSecretDataById(id);
		}
		updateData(userId);
	}
}