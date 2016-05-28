package com.playerdata.groupsecret;

import com.rwbase.dao.groupsecret.pojo.GroupSecretTeamDataHolder;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;

/*
 * @author HC
 * @date 2016年5月27日 下午9:28:03
 * @Description 
 */
public class GroupSecretTeamDataMgr {

	private static GroupSecretTeamDataMgr mgr = new GroupSecretTeamDataMgr();

	public static GroupSecretTeamDataMgr getMgr() {
		return mgr;
	}

	GroupSecretTeamDataMgr() {
	}

	/**
	 * 获取阵容信息
	 * 
	 * @param userId
	 * @return
	 */
	public GroupSecretTeamData get(String userId) {
		return GroupSecretTeamDataHolder.getHolder().get(userId);
	}

	/**
	 * 更新数据
	 * 
	 * @param userId
	 */
	public void update(String userId) {
		GroupSecretTeamDataHolder.getHolder().updateData(userId);
	}
}