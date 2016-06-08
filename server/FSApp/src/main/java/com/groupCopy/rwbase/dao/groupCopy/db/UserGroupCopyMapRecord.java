package com.groupCopy.rwbase.dao.groupCopy.db;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/**
 * 
 * 角色副本章节数据
 * @author allen
 *
 */
@Table(name = "user_group_copy_map_record")
@SynClass
public class UserGroupCopyMapRecord implements IMapItem {

	@Id
	private String id; // lid
	private String userId;	
	private String chaterID;//章节id
	
	private int fightCount;
	
	public String getChaterID() {
		return chaterID;
	}
	public void setChaterID(String chaterID) {
		this.chaterID = chaterID;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getFightCount() {
		return fightCount;
	}
	public void setFightCount(int fightCount) {
		this.fightCount = fightCount;
	}
	
	public void incrFightCount(){
		this.fightCount = this.fightCount+1;
	}
	

	
}
