package com.rwbase.dao.groupCopy.db;

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
@Table(name = "group_copy_map_item")
@SynClass
public class GroupCopyMapRecord implements IMapItem {

	@Id
	private String id; // 唯一id
	private String groupId; // 帮派ID
	
	private int level;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	
	
	
}
