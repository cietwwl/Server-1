package com.playerdata.groupsecret;

import com.rwbase.dao.groupsecret.pojo.GroupSecretMatchEnemyDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretMatchEnemyData;

/*
 * @author HC
 * @date 2016年5月27日 上午11:08:33
 * @Description 
 */
public class GroupSecretMatchEnemyDataMgr {
	private static GroupSecretMatchEnemyDataMgr mgr = new GroupSecretMatchEnemyDataMgr();

	public static GroupSecretMatchEnemyDataMgr getMgr() {
		return mgr;
	}

	GroupSecretMatchEnemyDataMgr() {
	}

	/**
	 * 获取敌人的数据
	 * 
	 * @param userId
	 * @return
	 */
	public GroupSecretMatchEnemyData get(String userId) {
		return GroupSecretMatchEnemyDataHolder.getHolder().get(userId);
	}

	public void delete(String userId) {
	}
}