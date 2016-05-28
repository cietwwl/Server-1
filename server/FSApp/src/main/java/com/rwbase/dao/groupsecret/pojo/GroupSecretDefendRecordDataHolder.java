package com.rwbase.dao.groupsecret.pojo;

import com.rwbase.dao.groupsecret.pojo.db.GroupSecretDefendRecordData;
import com.rwbase.dao.groupsecret.pojo.db.dao.GroupSecretDefendRecordDataDAO;

/*
 * @author HC
 * @date 2016年5月26日 下午5:23:03
 * @Description 帮派秘境防守记录的Holder
 */
public class GroupSecretDefendRecordDataHolder {
	private String userId;

	public GroupSecretDefendRecordDataHolder(String userId) {
		this.userId = userId;
	}

	/**
	 * 获取防守记录
	 * 
	 * @return
	 */
	public GroupSecretDefendRecordData get() {
		return GroupSecretDefendRecordDataDAO.getDAO().get(userId);
	}
}