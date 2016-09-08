package com.rwbase.dao.randomBoss.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.NonSave;


@Table(name="random_boss_record")
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class RandomBossRecord {

	
	@Id
	private String id;//userID+createTime
	
	//发现者id
	private String owerID;
	
	/**所有攻击过这个boss的角色和次数*/
	@IgnoreSynField
	private Map<String, Integer> fightRole = new HashMap<String, Integer>();
	
	private long leftHp;
	
	@IgnoreSynField
	private List<BattleNewsData> battleInfo = new ArrayList<BattleNewsData>();
	
	private String bossTemplateId;
	
	
	private long bornTime;
	
	/**最后一击角色*/
	@IgnoreSynField
	private long finalHitRole;
	
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

	
	public RandomBossRecord() {
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getOwerID() {
		return owerID;
	}


	public void setOwerID(String owerID) {
		this.owerID = owerID;
	}


	public Map<String, Integer> getFightRole() {
		return fightRole;
	}


	public void setFightRole(Map<String, Integer> fightRole) {
		this.fightRole = fightRole;
	}


	public long getLeftHp() {
		return leftHp;
	}


	public void setLeftHp(long leftHp) {
		this.leftHp = leftHp;
	}


	public List<BattleNewsData> getBattleInfo() {
		return battleInfo;
	}


	public void setBattleInfo(List<BattleNewsData> battleInfo) {
		this.battleInfo = battleInfo;
	}


	public String getBossTemplateId() {
		return bossTemplateId;
	}


	public void setBossTemplateId(String bossTemplateId) {
		this.bossTemplateId = bossTemplateId;
	}


	public long getBornTime() {
		return bornTime;
	}


	public void setBornTime(long bornTime) {
		this.bornTime = bornTime;
	}


	public long getFinalHitRole() {
		return finalHitRole;
	}


	public void setFinalHitRole(long finalHitRole) {
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
	
	
	
	
	
	
}
