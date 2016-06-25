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
	
	/**
	 * 添加新记录
	 * @param player
	 * @param chaterID
	 * @return
	 */
	public UserGroupCopyMapRecord addNewUserMapRecord(Player player, String chaterID){
		
		UserGroupCopyMapRecord record = getByLevel(chaterID);
		if(record == null){
			GroupCopyMapCfg cfg = GroupCopyMapCfgDao.getInstance().getCfgById(chaterID);
			
			record = new UserGroupCopyMapRecord(chaterID, userId, cfg.getEnterCount());
			holder.addItem(player, record);
		}
		
		return record;
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