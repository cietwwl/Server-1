package com.groupCopy.rwbase.dao.groupCopy.db;


import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.junit.Ignore;

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
	
	/**当前关卡的赞助*/
	@CombineSave
	private Map<String, Integer> buffMap = new HashMap<String, Integer>();
	
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
	public synchronized void addBuff(String playerID, int count) {
		Integer v = buffMap.get(playerID);
		if(v != null){
			buffMap.put(playerID, v + count);
		}else{
			buffMap.put(playerID, count);
		}
	}
	
	public synchronized void resetLevelData(){
		progress = new GroupCopyProgress();
		buffMap.clear();
	}
}
