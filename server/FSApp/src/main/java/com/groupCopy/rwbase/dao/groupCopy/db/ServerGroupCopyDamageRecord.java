package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import com.groupCopy.bm.groupCopy.GroupCopyMgr;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 帮派副本关卡全服伤害排行前10
 * @author Alex
 * 2016年6月12日 下午3:10:11
 */
@Table(name="server_group_copy_damage_record")
@SynClass
public class ServerGroupCopyDamageRecord implements IMapItem{

	@Id
	private String id; //对应关卡id
	private String groupId;
	
	@CombineSave
	private String levelID;
	
	//首次击杀
	@CombineSave
	private GroupCopyArmyDamageInfo firstKillInfo;
	
	@CombineSave
	private List<GroupCopyArmyDamageInfo> records = new ArrayList<GroupCopyArmyDamageInfo>();
	
	
	public ServerGroupCopyDamageRecord() {
	}



	public void setRecords(List<GroupCopyArmyDamageInfo> records) {
		this.records = records;
	}



	public ServerGroupCopyDamageRecord(String groupId, String levelID) {
		this.id = levelID;
		this.groupId = groupId;
		this.levelID = levelID;
	}



	public GroupCopyArmyDamageInfo getFirstKillInfo() {
		return firstKillInfo;
	}



	public void setFirstKillInfo(GroupCopyArmyDamageInfo firstKillInfo) {
		this.firstKillInfo = firstKillInfo;
	}



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



	public List<GroupCopyArmyDamageInfo> getRecords() {
		return records;
	}



	public void setRecords(LinkedList<GroupCopyArmyDamageInfo> records) {
		this.records = records;
	}



	public synchronized boolean checkOrAddRecord(GroupCopyArmyDamageInfo damageInfo, boolean kill) {
		if(kill && firstKillInfo == null){
			firstKillInfo = damageInfo;
		}else{
			GameLog.error(LogModule.GroupCopy, "ServerGroupCopyDamageRecord[checkOrAddRecord]", "检查帮派副本首次击杀数据时发现存在旧记录", null);
		}
		GroupCopyArmyDamageInfo tem = null;
		if(!records.isEmpty()){
			tem = records.get(records.size() - 1);
			if(tem.getDamage() >= damageInfo.getDamage()){
				return false;
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
				return false;
			}else{
				records.remove(tem);
			}
		}
		
		records.add(damageInfo);
		Collections.sort(records, GroupCopyMgr.RANK_COMPARATOR);
		if(records.size() > GroupCopyMgr.MAX_RANK_RECORDS){
			records.remove(records.size() - 1);
		}
		return true;
	}

	
	
}
