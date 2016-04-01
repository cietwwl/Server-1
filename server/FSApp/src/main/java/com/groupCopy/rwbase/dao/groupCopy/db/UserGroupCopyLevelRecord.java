package com.groupCopy.rwbase.dao.groupCopy.db;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;

/**
 * 时装信息
 * 
 * @author allen
 *
 */
@Table(name = "user_group_copy_level_record")
@SynClass
public class UserGroupCopyLevelRecord implements IMapItem {

	@Id
	private String id; // lid
	private String userId;	
	private String level;
	
	private int fightCount;
	
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
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
