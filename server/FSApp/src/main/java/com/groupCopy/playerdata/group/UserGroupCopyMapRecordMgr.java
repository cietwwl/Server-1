package com.groupCopy.playerdata.group;

import java.util.List;

import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyMapRecord;
import com.groupCopy.rwbase.dao.groupCopy.db.UserGroupCopyMapRecordHolder;
import com.playerdata.Player;


public class UserGroupCopyMapRecordMgr {
	
	private UserGroupCopyMapRecordHolder holder;// 个人帮派数据的管理
	@SuppressWarnings("unused")
	private String userId;// 成员Id

	public UserGroupCopyMapRecordMgr(String userId) {
		this.userId = userId;
		holder = new UserGroupCopyMapRecordHolder(userId);
	}


	public List<UserGroupCopyMapRecord> getUserMapRecordList(){
		return holder.getItemList();
	}
	
	public UserGroupCopyMapRecord getByLevel(String level){
		return holder.getByLevel(level);
	}
	
	public int getDataVersion(){
		return holder.getVersion();
	}
	
	public boolean updateItem(Player player,UserGroupCopyMapRecord item){
		return holder.updateItem(player, item);
	}


	public void resetDataInNewDay() {
		
		holder.resetFightCount();
	}
	
	public void syncData(Player player){
		holder.syncData(player);
	}

}