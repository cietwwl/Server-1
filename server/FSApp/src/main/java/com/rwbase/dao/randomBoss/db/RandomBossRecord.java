package com.rwbase.dao.randomBoss.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.bm.randomBoss.RandomBossMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rw.fsutil.util.DateUtils;


@Table(name="random_boss_record")
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class RandomBossRecord {

	
	@Id
	private String id;//userID+createTime
	
	//发现者id
	private String ownerID;
	
	private String ownerName;
	
	/**所有攻击过这个boss的角色和次数*/
	@IgnoreSynField
	private ConcurrentHashMap<String, Integer> fightRole = new ConcurrentHashMap<String, Integer>();
	
	private long leftHp;
	
	@IgnoreSynField
	private ConcurrentLinkedQueue<BattleNewsData> battleInfo = new ConcurrentLinkedQueue<BattleNewsData>();
	
	private String bossTemplateId;
	
	/**boss逃离时间点*/
	private long excapeTime;
	
	/**最后一击角色*/
	@IgnoreSynField
	private String finalHitRole;
	
	/**
	 * 角色与此boss战斗的次数，此字段不保存进数据库，只有在和前端同步的时候才赋值
	 */
	@NonSave
	private int battleTime;
	
	/**
	 * 最后一击角色名，同步到前端时根据finalHitRole赋值
	 */
	@NonSave
	private String finalHitRoleName;

	/**
	 * 上次战斗时间，不保存入数据库，不同步到前端，只是服务器逻辑用
	 */
	@IgnoreSynField
	@NonSave
	private long lastBattleTime;
	
	/**
	 * 与boss战斗角色id,战斗结束后消除
	 */
	@IgnoreSynField
	@NonSave
	private String battleRoleID;
	
	//上次发送好友邀请时间
	@NonSave
	private long lastFriendInvitedTime;
	
	
	//上次发送帮派邀请时间
	@NonSave
	private long lastGroupInvitedTime;
	
	public RandomBossRecord() {
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getOwnerID() {
		return ownerID;
	}


	public void setOwnerID(String owerID) {
		this.ownerID = owerID;
	}


	public Map<String, Integer> getFightRole() {
		return new HashMap<String, Integer>(fightRole);
	}


	public void setFightRole(Map<String, Integer> fightRole) {
		this.fightRole.clear();
		this.fightRole.putAll(fightRole);
	}


	public long getLeftHp() {
		return leftHp;
	}

	
	

	public void setLeftHp(long leftHp) {
		this.leftHp = leftHp;
	}


	public List<BattleNewsData> getBattleInfo() {
		return new ArrayList<BattleNewsData>(battleInfo);
	}


	public void setBattleInfo(List<BattleNewsData> battleInfo) {
		this.battleInfo.clear();
		this.battleInfo.addAll(battleInfo);
	}


	public String getBossTemplateId() {
		return bossTemplateId;
	}


	public void setBossTemplateId(String bossTemplateId) {
		this.bossTemplateId = bossTemplateId;
	}


	


	public String getFinalHitRole() {
		return finalHitRole;
	}


	public void setFinalHitRole(String finalHitRole) {
		this.finalHitRole = finalHitRole;
	}


	public int getBattleTime() {
		return battleTime;
	}


	public void setBattleTime(int battleTime) {
		this.battleTime = battleTime;
	}


	public String getFinalHitRoleName() {
		return finalHitRoleName;
	}


	public void setFinalHitRoleName(String finalHitRoleName) {
		this.finalHitRoleName = finalHitRoleName;
	}


	public long getExcapeTime() {
		return excapeTime;
	}


	public void setExcapeTime(long excapeTime) {
		this.excapeTime = excapeTime;
	}

	public String getOwnerName() {
		return ownerName;
	}


	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public long getLastBattleTime() {
		return lastBattleTime;
	}


	public void setLastBattleTime(long time) {
		this.lastBattleTime = time;
	}

	public String getBattleRoleID() {
		return battleRoleID;
	}
	
	public void setBattleRoleID(String battleRoleID) {
		this.battleRoleID = battleRoleID;
	}


	public void addBattleInfo(BattleNewsData e){
		battleInfo.add(e);
	}


	public long getLastFriendInvitedTime() {
		return lastFriendInvitedTime;
	}


	public void setLastFriendInvitedTime(long lastFriendInvitedTime) {
		this.lastFriendInvitedTime = lastFriendInvitedTime;
	}


	public long getLastGroupInvitedTime() {
		return lastGroupInvitedTime;
	}


	public void setLastGroupInvitedTime(long lastGroudInvitedTime) {
		this.lastGroupInvitedTime = lastGroudInvitedTime;
	}

		
	
	//----------------------------------操作逻辑---------------------------------------------//
	
	public RandomBossRecord clone() {
		RandomBossRecord record = new RandomBossRecord();
		record.setId(id);
		record.setOwnerID(ownerID);
		if(StringUtils.isNotBlank(ownerID) && StringUtils.isBlank(ownerName)){
			Player owner = PlayerMgr.getInstance().find(ownerID);
			if(!StringUtils.equals(ownerName, owner.getUserName())){
				ownerName = owner.getUserName();
			}
		}
		record.setLastFriendInvitedTime(lastFriendInvitedTime);
		record.setLastGroupInvitedTime(lastGroupInvitedTime);
		record.setOwnerName(ownerName);
		record.setLeftHp(leftHp);
		record.setBossTemplateId(bossTemplateId);
		record.setExcapeTime(excapeTime);
		if(!StringUtils.isBlank(finalHitRole) && leftHp <= 0){
			Player p = PlayerMgr.getInstance().find(finalHitRole);
			finalHitRoleName = p.getUserName();
			record.setFinalHitRoleName(finalHitRoleName);
		}
		
		
		return record;
	}
	
	/**
	 * 获取角色与boss战斗次数
	 * @param userID
	 * @return
	 */
	public int roleFightBossCount(String userID){
		Integer count = fightRole.get(userID);
		return count == null ? 0 : count;
	}



	/**
	 * 设置上次战斗时间，如果true，则表示可以进入战斗，如果false,表示上次战斗还没有结束
	 * @return
	 */
	public synchronized boolean resetLastBattleTime() {
		long nowTime = System.currentTimeMillis();
		long endTime = lastBattleTime + RandomBossMgr.getInstance().getBattleTimeLimit();
		if(nowTime < endTime){
//			System.err.println("apply enter random boss fight, current time:" + DateUtils.getDateTimeFormatString(nowTime, "yyyy-MM-dd HH:mm:ss")
//					+",endTime:"+ DateUtils.getDateTimeFormatString(endTime, "yyyy-MM-dd HH:mm:ss"));
			return false;
		}
		lastBattleTime = nowTime;
		return true;
	}

	public void battleEnd(String roleID){
		lastBattleTime = 0;
		battleRoleID = "";
		Integer count = fightRole.get(roleID);
		if(count == null){
			count = 1;
		}else{
			count ++;
		}
		fightRole.put(roleID, count);
	}


	

	
	


	
	
}
