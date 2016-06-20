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
	private String id; // 对应章节id
	
	private String userId;
	
	private int fightCount;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	

	
}
