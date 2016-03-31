package com.groupCopy.playerdata.group;

import java.util.List;

import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyLevelRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyLevelRecordHolder;


public class UserGroupCopyLevelRecordMgr {
	
	private UserGroupCopyLevelRecordHolder holder;// 个人帮派数据的管理
	@SuppressWarnings("unused")
	private String userId;// 成员Id

	public UserGroupCopyLevelRecordMgr(String userId) {
		this.userId = userId;
		holder = new UserGroupCopyLevelRecordHolder(userId);
	}


	public List<UserGroupCopyLevelRecord> getRecordList(){
		return holder.getItemList();
	}
	
	public UserGroupCopyLevelRecord getByLevel(String level){
		return holder.getByLevel(level);
	}

}