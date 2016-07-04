package com.groupCopy.rwbase.dao.groupCopy.db;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCopyMapRecord implements IMapItem {

	@Id
	private String id; // chaterID_groupID
	@IgnoreSynField
	private String groupId; // 帮派ID
	
	@CombineSave
	private String chaterID; //对应章节id
	
	@CombineSave
	private GroupCopyMapStatus status;//状态 开启 关闭 完成
	
	@CombineSave
	private long rewardTime;//结束奖励时间点ms
	
	@CombineSave
	private double progress;//进度  这个进度应该是由关卡决定
	
	
	//帮派副本地图前10伤害排行榜(单次伤害)
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
	
	public void addPlayerDamage(String playerName, int damage){
		Integer v = groupRoleDamageMap.get(playerName);
		if(v != null){
			groupRoleDamageMap.put(playerName, v + damage);
		}else{
			groupRoleDamageMap.put(playerName, damage);
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

	public String getChaterID() {
		return chaterID;
	}
	public void setChaterID(String chaterID) {
		this.chaterID = chaterID;
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
	
	
	public long getRewardTime() {
		return rewardTime;
	}
	public void setRewardTime(long rewardTime) {
		this.rewardTime = rewardTime;
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
