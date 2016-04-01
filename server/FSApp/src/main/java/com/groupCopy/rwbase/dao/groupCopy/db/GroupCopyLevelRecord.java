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
@Table(name = "group_copy_level_item")
@SynClass
public class GroupCopyLevelRecord implements IMapItem {

	@Id
	private String id; // 唯一id
	private String groupId; // 帮派ID
	
	@CombineSave
	private String level;
	@CombineSave
	private int progress;
	@CombineSave
	private long lastBeginFightTime;//上次战斗的时间
	@CombineSave
	private boolean isFighting;//是否在战斗中
	@CombineSave	
	private String fighterId;//挑战者Id
	
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void addProgress(int delta){
		this.progress = this.progress+delta;
	}
	public long getLastBeginFightTime() {
		return lastBeginFightTime;
	}
	public void setLastBeginFightTime(long lastBeginFightTime) {
		this.lastBeginFightTime = lastBeginFightTime;
	}
	public boolean isFighting() {
		return isFighting;
	}
	public void setFighting(boolean isFighting) {
		this.isFighting = isFighting;
	}
	public String getFighterId() {
		return fighterId;
	}
	public void setFighterId(String fighterId) {
		this.fighterId = fighterId;
	}
	
	
	
}
