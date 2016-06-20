package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.LinkedList;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
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
	@IgnoreSynField
	private String id; // 对应帮派id
	
	@IgnoreSynField
	private String groupId;
	
	
	/**分配记录，上限为40条*/
	@CombineSave
	private LinkedList<DistRewRecordItem> recordList = new LinkedList<DistRewRecordItem>();
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

	public LinkedList<DistRewRecordItem> getRecordList() {
		return recordList;
	}
	public void setRecordList(LinkedList<DistRewRecordItem> recordList) {
		this.recordList = recordList;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	
}
