package com.groupCopy.playerdata.group;

import java.util.List;

import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyMapRecordHolder;


public class UserGroupCopyMapRecordMgr {
	
	private UserGroupCopyMapRecordHolder holder;// 个人帮派数据的管理
	@SuppressWarnings("unused")
	private String userId;// 成员Id

	public UserGroupCopyMapRecordMgr(String userId) {
		this.userId = userId;
		holder = new UserGroupCopyMapRecordHolder(userId);
	}


	public List<UserGroupCopyMapRecord> getRecordList(){
		return holder.getItemList();
	}
	
	public UserGroupCopyMapRecord getByLevel(String level){
		return holder.getByLevel(level);
	}
	
	public int getDataVersion(){
		return holder.getVersion();
	}
	
	public boolean updateItem(UserGroupCopyMapRecord item){
		return holder.updateItem(item);
	}


	public void resetDataInNewDay() {
		
		holder.resetFightCount();
	}

}