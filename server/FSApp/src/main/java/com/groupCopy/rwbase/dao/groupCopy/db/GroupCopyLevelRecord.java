package com.groupCopy.rwbase.dao.groupCopy.db;


import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 帮派副本关卡记录
 * 
 * @author allen
 *
 */
@Table(name = "group_copy_level_item")
@SynClass
public class GroupCopyLevelRecord implements IMapItem {

	@Id
	private String id; // 唯一id 关卡id
	private String groupId; // 帮派ID
	
	@CombineSave
	private String level;
	
	/**
	 * 副本进度
	 */
	@CombineSave
	private GroupCopyProgress progress;
	
	@CombineSave
	private long lastBeginFightTime;//上次战斗的时间
	
	@CombineSave
	private boolean isFighting;//是否在战斗中
	@CombineSave	
	private String fighterId;//挑战者Id
	
	/**当前关卡的赞助*/
	@CombineSave
	private GroupCopyLevelBuffRecord buffRecord;
	
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
	
	public GroupCopyProgress getProgress() {
		return progress;
	}
	public void setProgress(GroupCopyProgress progress) {
		this.progress = progress;
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
	public GroupCopyLevelBuffRecord getBuffRecord() {
		return buffRecord;
	}
	public void setBuffRecord(GroupCopyLevelBuffRecord buffRecord) {
		this.buffRecord = buffRecord;
	}

	/**
	 * <pre>
	 * 添加buff
	 * <b>注意，此方法并不保证线程安全，要求外部进行并发控制</b>
	 * <pre>
	 * @param playerID
	 * @param count
	 */
	public void addBuff(String playerID, int count){
		if(buffRecord == null){
			buffRecord  = new GroupCopyLevelBuffRecord();
		}
		buffRecord.addBuff(playerID, count);
	}
	
	
	public synchronized void resetLevelData(){
		progress = new GroupCopyProgress();
		buffRecord.clearBuff();
	}
}
