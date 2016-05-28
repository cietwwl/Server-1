package com.rwbase.dao.groupsecret.pojo.db;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.rw.fsutil.dao.annotation.NonSave;
import com.rwbase.dao.groupsecret.pojo.db.data.DefendRecord;

/*
 * @author HC
 * @date 2016年5月26日 下午2:53:06
 * @Description 
 */
public class GroupSecretDefendRecordData {
	private String userId;// 角色的
	private Map<Integer, DefendRecord> recordList;// 记录的List
	@NonSave
	private AtomicInteger generateId;// 生成唯一Id

	// ////////////////////////////////////////////////逻辑Get区
	public String getUserId() {
		return userId;
	}

	public Map<Integer, DefendRecord> getRecordList() {
		return recordList;
	}

	// ////////////////////////////////////////////////逻辑Set区
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setRecordList(Map<Integer, DefendRecord> recordList) {
		this.recordList = recordList;
	}

	// ////////////////////////////////////////////////逻辑区
}