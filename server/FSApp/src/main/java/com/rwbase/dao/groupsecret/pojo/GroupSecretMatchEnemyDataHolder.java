package com.rwbase.dao.groupsecret.pojo;

import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;
import com.rwbase.dao.groupsecret.pojo.db.dao.GroupSecretMatchEnemyDataDAO;

/*
 * @author HC
 * @date 2016年5月26日 下午5:29:53
 * @Description 秘境匹配到的敌人信息Holder
 */
public class GroupSecretMatchEnemyDataHolder {
	private static GroupSecretMatchEnemyDataHolder holder = new GroupSecretMatchEnemyDataHolder();

	public static GroupSecretMatchEnemyDataHolder getHolder() {
		return holder;
	}

	GroupSecretMatchEnemyDataHolder() {
	}

	/**
	 * 获取秘境对应的敌人信息
	 * 
	 * @return
	 */
	public GroupSecretMatchEnemyData get(String userId) {
		return GroupSecretMatchEnemyDataDAO.getDAO().get(userId);
	}

	/**
	 * 更新数据
	 * 
	 * @param userId
	 */
	public void updateData(String userId) {
		GroupSecretMatchEnemyDataDAO.getDAO().update(userId);
	}
}