package com.groupCopy.rwbase.dao.groupCopy.db;


import javax.persistence.Id;
import javax.persistence.Table;

import com.groupCopy.bm.groupCopy.GroupCopyLevelBL;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
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
	private String id; // 唯一id 对应关卡id
	@IgnoreSynField
	private String groupId; // 帮派ID
	
	/**
	 * 副本进度
	 */
	@CombineSave
	private GroupCopyProgress progress;
	
	@CombineSave
	private long lastBeginFightTime;//上次战斗的时间
	
	@CombineSave
	@IgnoreSynField
	private int status;//是否在战斗中
	@CombineSave	
	@IgnoreSynField
	private String fighterId;//挑战者Id
	
	@CombineSave
	private CopyBuffInfo buffInfo = new CopyBuffInfo();
	
	
	public GroupCopyLevelRecord() {
	}
	
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


	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getFighterId() {
		return fighterId;
	}
	public void setFighterId(String fighterId) {
		this.fighterId = fighterId;
	}
	

	/**
	 * <pre>
	 * 添加buff
	 * <b>注意，此方法并不保证线程安全，要求外部进行并发控制</b>
	 * <pre>
	 * @param playerID
	 * @param count
	 */
	public void addRoleDonate(String playerID, int count) {
		buffInfo.addBuff(playerID, count);
	}
	
	public void addBuff(int count){
		buffInfo.increTotalBuff(count);
	}
	
	public int getBuffCount(){
		return buffInfo.getTotalBuff();
	}
	
	
	public void resetLevelData() {
		if(progress == null){
			progress = GroupCopyLevelBL.createProgress(id);
			return;
		}
		progress.reset();

	}
	public CopyBuffInfo getBuffInfo() {
		return buffInfo;
	}
	public void setBuffInfo(CopyBuffInfo buffInfo) {
		this.buffInfo = buffInfo;
	}
	
	
}
