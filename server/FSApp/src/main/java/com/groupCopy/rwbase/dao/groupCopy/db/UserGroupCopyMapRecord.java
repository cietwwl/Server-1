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
	
	private int leftFightCount;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getLeftFightCount() {
		return leftFightCount;
	}
	public void setLeftFightCount(int fightCount) {
		this.leftFightCount = fightCount;
	}
	
	public void incrFightCount(){
		if(leftFightCount > 0){
			this.leftFightCount -= 1;
		}
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	

	
}
