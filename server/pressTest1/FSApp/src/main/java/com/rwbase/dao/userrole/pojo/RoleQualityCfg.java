package com.rwbase.dao.userrole.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "role_quality_cfg")
public class RoleQualityCfg {

	private String id;
	private int jobType; // 职业...
	private int quality;//品质
	private int maxEnergyVal; // 最大能量值...
	private int maxHP; // 最大生命值...
	private int physiqueAttackVal; // 体魄攻击...
	private int spiritAttackVal; // 精神攻击...
	private int physiqueDef; // 体魄防御...
	private int spiritDef; // 精神防御...
	private int attackVampire; // 攻击吸血...
	private int criticalVal; // 暴击率...
	private int criHamPromot; // 暴击伤害提升...
	private int toughnessVal; // 韧性...
	private int hPReceiveVal; // 生命回复...
	private int energyReceiveVal; // 能量回复...
	private int energyTransVal; // 能量转化...
	private float attackDistance; // 攻击距离...
	private float attackSpeed; // 攻击速度...
	private float moveSpeed; // 移动速度...
	
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getJobType() {
		return jobType;
	}
	public void setJobType(int jobType) {
		this.jobType = jobType;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public int getMaxEnergyVal() {
		return maxEnergyVal;
	}
	public void setMaxEnergyVal(int maxEnergyVal) {
		this.maxEnergyVal = maxEnergyVal;
	}
	public int getMaxHP() {
		return maxHP;
	}
	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}
	public int getPhysiqueAttackVal() {
		return physiqueAttackVal;
	}
	public void setPhysiqueAttackVal(int physiqueAttackVal) {
		this.physiqueAttackVal = physiqueAttackVal;
	}
	public int getSpiritAttackVal() {
		return spiritAttackVal;
	}
	public void setSpiritAttackVal(int spiritAttackVal) {
		this.spiritAttackVal = spiritAttackVal;
	}
	public int getPhysiqueDef() {
		return physiqueDef;
	}
	public void setPhysiqueDef(int physiqueDef) {
		this.physiqueDef = physiqueDef;
	}
	public int getSpiritDef() {
		return spiritDef;
	}
	public void setSpiritDef(int spiritDef) {
		this.spiritDef = spiritDef;
	}
	public int getAttackVampire() {
		return attackVampire;
	}
	public void setAttackVampire(int attackVampire) {
		this.attackVampire = attackVampire;
	}
	public int getCriticalVal() {
		return criticalVal;
	}
	public void setCriticalVal(int criticalVal) {
		this.criticalVal = criticalVal;
	}
	public int getCriHamPromot() {
		return criHamPromot;
	}
	public void setCriHamPromot(int criHamPromot) {
		this.criHamPromot = criHamPromot;
	}
	public int getToughnessVal() {
		return toughnessVal;
	}
	public void setToughnessVal(int toughnessVal) {
		this.toughnessVal = toughnessVal;
	}
	public int gethPReceiveVal() {
		return hPReceiveVal;
	}
	public void sethPReceiveVal(int hPReceiveVal) {
		this.hPReceiveVal = hPReceiveVal;
	}
	public int getEnergyReceiveVal() {
		return energyReceiveVal;
	}
	public void setEnergyReceiveVal(int energyReceiveVal) {
		this.energyReceiveVal = energyReceiveVal;
	}
	public int getEnergyTransVal() {
		return energyTransVal;
	}
	public void setEnergyTransVal(int energyTransVal) {
		this.energyTransVal = energyTransVal;
	}
	public float getAttackDistance() {
		return attackDistance;
	}
	public void setAttackDistance(float attackDistance) {
		this.attackDistance = attackDistance;
	}
	public float getAttackSpeed() {
		return attackSpeed;
	}
	public void setAttackSpeed(float attackSpeed) {
		this.attackSpeed = attackSpeed;
	}
	public float getMoveSpeed() {
		return moveSpeed;
	}
	public void setMoveSpeed(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}
}
