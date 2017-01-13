package com.rwbase.dao.groupCopy.db;

import java.util.LinkedList;
import java.util.List;

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
public class GroupCopyRewardDistRecord implements IMapItem {

	@Id
	private String id; // 对应帮派id
	
	private String groupId;
	
	
	/**分配记录，上限为40条*/
	@CombineSave
	private List<DistRewRecordItem> recordList = new LinkedList<DistRewRecordItem>();
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

	public List<DistRewRecordItem> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<DistRewRecordItem> recordList) {
		this.recordList = recordList;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	
	public synchronized void addRecord(DistRewRecordItem item){
		if(recordList.size() >= 40)
			recordList.remove(0);
		recordList.add(item);
	}
	
}
