package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 帮派奖励分配信息
 * 
 * @author allen
 *
 */
@Table(name = "group_copy_reward_record")
@SynClass
public class GroupCopyRewardRecord implements IMapItem {

	@Id
	private String id; // 唯一id
	private String groupId; // 帮派ID
	
	@CombineSave
	private int level;
	
	/**分配记录，上限为40条，超出部分移除最旧记录<key=itemID,value=内容>*/
	@CombineSave
	private LinkedHashMap<String, String> recordList = new LinkedHashMap<String, String>();
	
	
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
	public LinkedHashMap<String, String> getRecordList() {
		return recordList;
	}
	public void setRecordList(LinkedHashMap<String, String> recordList) {
		this.recordList = recordList;
	}

	
	
	
}
