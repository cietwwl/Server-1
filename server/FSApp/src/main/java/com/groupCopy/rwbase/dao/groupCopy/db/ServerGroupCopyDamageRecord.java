package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.Collections;
import java.util.LinkedList;

import javax.persistence.Id;
import javax.persistence.Table;

import com.groupCopy.bm.groupCopy.GroupCopyMgr;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 帮派副本关卡全服伤害排行前10
 * @author Alex
 * 2016年6月12日 下午3:10:11
 */
@Table(name="server_group_copy_damage_record")
public class ServerGroupCopyDamageRecord implements IMapItem{

	@Id
	private String id;
	private String groupId;
	
	@CombineSave
	private String levelID;
	
	//首次击杀
	@CombineSave
	private GroupCopyArmyDamageInfo firstKillInfo;
	@CombineSave
	private LinkedList<GroupCopyArmyDamageInfo> records = new LinkedList<GroupCopyArmyDamageInfo>();
	
	
	@Override
	public String getId() {
		return id;
	}



	public String getGroupId() {
		return groupId;
	}



	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getLevelID() {
		return levelID;
	}



	public void setLevelID(String levelID) {
		this.levelID = levelID;
	}



	public LinkedList<GroupCopyArmyDamageInfo> getRecords() {
		return records;
	}



	public void setRecords(LinkedList<GroupCopyArmyDamageInfo> records) {
		this.records = records;
	}



	public synchronized void checkOrAddRecord(GroupCopyArmyDamageInfo damageInfo) {
		GroupCopyArmyDamageInfo tem = null;
		if(!records.isEmpty()){
			tem = records.getLast();
			if(tem.getDamage() >= damageInfo.getDamage()){
				return;
			}
		}
		
		//检查一下是否有记录
		for (GroupCopyArmyDamageInfo info : records) {
			if(info.getPlayerID() == damageInfo.getPlayerID()){
				tem = info;
			}
		}
		if(tem != null){
			if(tem.getDamage() >= damageInfo.getDamage()){
				return;
			}else{
				records.remove(tem);
			}
		}
		
		records.add(damageInfo);
		Collections.sort(records, GroupCopyMgr.RANK_COMPARATOR);
		if(records.size() > GroupCopyMgr.MAX_RANK_RECORDS){
			records.removeLast();
		}
	}

	
	
}
