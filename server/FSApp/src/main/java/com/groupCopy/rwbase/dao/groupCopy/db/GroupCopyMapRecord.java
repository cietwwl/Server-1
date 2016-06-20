package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rwproto.GroupCopyCmdProto.GroupCopyMapStatus;

/**
 * 
 * 
 * @author allen
 *
 */
@Table(name = "group_copy_map_item")
@SynClass
public class GroupCopyMapRecord implements IMapItem {

	@Id
	private String id; // 对应章节id
	@IgnoreSynField
	private String groupId; // 帮派ID
	
	@CombineSave
	private GroupCopyMapStatus status;//状态 开启 关闭 完成
	
	@CombineSave
	private long rewardTime;//结束奖励时间点ms
	
	@CombineSave
	private double progress;//进度  这个进度应该是由关卡决定
	
	
	/**当前地图的掉落物品及对应的申请列表<key=itemID,value=掉落记录>*/
	@CombineSave
	@IgnoreSynField
	private ConcurrentHashMap<Integer, GroupCopyMapItemDropAndApplyRecord> daMap = new ConcurrentHashMap<Integer, GroupCopyMapItemDropAndApplyRecord>();
	
	@CombineSave
	@IgnoreSynField
	private GroupCopyDamegeRankInfo damegeRankInfo = new GroupCopyDamegeRankInfo();
	
	/**帮派成员在此章节总伤害，章节重置后清除<key=playerId,value=totalDamage>*/
	@CombineSave
	@IgnoreSynField
	private Map<String, Integer> groupRoleDamageMap = new HashMap<String, Integer>();
	
	
	
	public Map<String, Integer> getGroupRoleDamageMap() {
		return groupRoleDamageMap;
	}
	public void setGroupRoleDamageMap(Map<String, Integer> groupRoleDamageMap) {
		this.groupRoleDamageMap = groupRoleDamageMap;
	}
	
	public void addPlayerDamage(String playerID, int damage){
		Integer v = groupRoleDamageMap.get(playerID);
		if(v != null){
			groupRoleDamageMap.put(playerID, v + damage);
		}else{
			groupRoleDamageMap.put(playerID, damage);
		}
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

	public GroupCopyMapStatus getStatus() {
		return status;
	}
	public void setStatus(GroupCopyMapStatus status) {
		this.status = status;
	}
	public double getProgress() {
		return progress;
	}
	public void setProgress(double progress) {
		this.progress = progress;
	}
	public GroupCopyDamegeRankInfo getDamegeRankInfo() {
		return damegeRankInfo;
	}
	public void setDamegeRankInfo(GroupCopyDamegeRankInfo damegeRankInfo) {
		this.damegeRankInfo = damegeRankInfo;
	}
	
	public long getRewardTime() {
		return rewardTime;
	}
	public void setRewardTime(long rewardTime) {
		this.rewardTime = rewardTime;
	}
	/**
	 * 此方法只是提供序列化使用，一般功能禁止调用，
	 * 如需迭代，请使用{@link GroupCopyMapRecord#getDropApplyEnumeration()}
	 * */
	public ConcurrentHashMap<Integer, GroupCopyMapItemDropAndApplyRecord> getDaMap() {
		return daMap;
	}
	
	/**此方法只是提供序列化使用，一般功能禁止调用*/
	public void setDaMap(
			ConcurrentHashMap<Integer, GroupCopyMapItemDropAndApplyRecord> daMap) {
		this.daMap = daMap;
	}
	
	public Enumeration<GroupCopyMapItemDropAndApplyRecord> getDropApplyEnumeration(){
		return daMap.elements();
	}

	public GroupCopyMapItemDropAndApplyRecord getDropApplyRecord(int key){
		return daMap.get(key);
	}
	
	public boolean checkOrAddDamageRank(GroupCopyArmyDamageInfo info){
		return damegeRankInfo.addInfo(info);
	}

	/**
	 * 开启或重置时消除旧数据
	 */
	public void cleanData(){
		progress = 0;
		damegeRankInfo.clear();
		groupRoleDamageMap.clear();
	}
	
}
