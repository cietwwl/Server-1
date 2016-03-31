package com.groupCopy.rwbase.dao.groupCopy.db;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

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
	
	@CombineSave
	private int level;
	@CombineSave
	private GroupCopyStatus status;//状态 开启 关闭 完成	
	@CombineSave
	private int progress;//进度
	
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
	public GroupCopyStatus getStatus() {
		return status;
	}
	public void setStatus(GroupCopyStatus status) {
		this.status = status;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}


	
}
