package com.groupCopy.playerdata.group;

import java.util.List;

import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
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
	
	public UserGroupCopyMapRecord getByChaterID(String id){
		return holder.getItemByID(id);
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
	
	//作弊添加次数
	public void setRoleBattleTime(int count, Player player){
		holder.setFigntCount(count, player);
	}

}