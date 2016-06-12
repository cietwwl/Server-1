package com.rwbase.dao.groupsecret.pojo;

import com.rwbase.dao.groupsecret.pojo.db.GroupSecretTeamData;
import com.rwbase.dao.groupsecret.pojo.db.dao.GroupSecretTeamDataDAO;

/*
 * @author HC
 * @date 2016年5月26日 下午5:25:39
 * @Description 秘境阵容信息Holder
 */
public class GroupSecretTeamDataHolder {
	private static GroupSecretTeamDataHolder holder = new GroupSecretTeamDataHolder();

	public static GroupSecretTeamDataHolder getHolder() {
		return holder;
	}

	GroupSecretTeamDataHolder() {
	}

	/**
	 * 获取秘境对应的阵容信息
	 * 
	 * @return
	 */
	public GroupSecretTeamData get(String userId) {
		return GroupSecretTeamDataDAO.getDAO().get(userId);
	}

	/**
	 * 刷新数据
	 * 
	 * @param userId
	 */
	public void updateData(String userId) {
		GroupSecretTeamDataDAO.getDAO().update(userId);
	}
}