package com.rwbase.dao.battle.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_user_battle_force")
public class RoleBattleForce {
	
	@Id
	private String userId; // id
	private int jobType; // 职业...
	private int baseLife; // 生命值...
	private int energyVal; // 能量值...
	private int physiqueAttackVal; // 体魄攻击...
	private int spiritAttackVal; // 精神攻击...
	private int physiqueDef; // 体魄防御...
	private int spiritDef; // 精神防御...
	private int attackVampire; // 攻击吸血...
	private int criticalVal; // 暴击率...
	private int criHamPromot; // 暴击伤害提升...
	private int toughnessVal; // 韧性...
	private int lifeReceiveVal; // 生命回复...
	private int energyReceiveVal; // 能量回复...
	private int energyTransVal; // 能量转化...
	private float attackDistance; // 攻击距离...
	private float attackSpeed; // 攻击速度...
	private float moveSpeed; // 移动速度...
	
	private float battleForce;//主角战力
	
	private int lifeGrowUp; // 生命成长...
	private int attackGrowUp; // 攻击成长...
	private int physicqueDefGrouUp; // 体魄防御成长...
	private int spiritDefGrowUP; // 精神防御成长...
	
	private int baseStar;//星级
	private int quality;//品质
	
	
	public RoleBattleForce() {
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getJobType() {
		return jobType;
	}
	public void setJobType(int jobType) {
		this.jobType = jobType;
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

	public float getBattleForce() {
		return battleForce;
	}

	public void setBattleForce(float battleForce) {
		this.battleForce = battleForce;
	}

	public int getLifeReceiveVal() {
		return lifeReceiveVal;
	}

	public void setLifeReceiveVal(int lifeReceiveVal) {
		this.lifeReceiveVal = lifeReceiveVal;
	}

	public int getLifeGrowUp() {
		return lifeGrowUp;
	}

	public void setLifeGrowUp(int lifeGrowUp) {
		this.lifeGrowUp = lifeGrowUp;
	}

	public int getAttackGrowUp() {
		return attackGrowUp;
	}

	public void setAttackGrowUp(int attackGrowUp) {
		this.attackGrowUp = attackGrowUp;
	}

	public int getPhysicqueDefGrouUp() {
		return physicqueDefGrouUp;
	}

	public void setPhysicqueDefGrouUp(int physicqueDefGrouUp) {
		this.physicqueDefGrouUp = physicqueDefGrouUp;
	}

	public int getSpiritDefGrowUP() {
		return spiritDefGrowUP;
	}

	public void setSpiritDefGrowUP(int spiritDefGrowUP) {
		this.spiritDefGrowUP = spiritDefGrowUP;
	}

	public int getBaseStar() {
		return baseStar;
	}

	public void setBaseStar(int baseStar) {
		this.baseStar = baseStar;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getBaseLife() {
		return baseLife;
	}

	public void setBaseLife(int baseLife) {
		this.baseLife = baseLife;
	}

	public int getEnergyVal() {
		return energyVal;
	}

	public void setEnergyVal(int energyVal) {
		this.energyVal = energyVal;
	}
	
}
