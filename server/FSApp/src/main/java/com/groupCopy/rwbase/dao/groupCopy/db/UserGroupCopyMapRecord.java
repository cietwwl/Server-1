package com.groupCopy.rwbase.dao.groupCopy.db;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

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
	private String id; // userId_chaterID
	
	@IgnoreSynField
	private String userId;
	
	@CombineSave
	private String chaterID;
	
	@CombineSave
	private int leftFightCount;
	
	
	

	public UserGroupCopyMapRecord() {
	}
	
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

	public String getChaterID() {
		return chaterID;
	}

	public void setChaterID(String chaterID) {
		this.chaterID = chaterID;
	}
	

	
}
