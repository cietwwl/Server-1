package com.rwbase.dao.item.pojo;

import java.util.Map;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;

public class GemCfg extends ItemBaseCfg {
	private int life; // 最大生命值提升
	private int attack; // 攻击提升
	private int physiqueDef; // 体魄防御提升
	private int spiritDef; // 精神防御提升
	private int attackVampire; // 攻击吸血提升
	private int critical; // 暴击率提升
	private int criticalHurt; // 暴击伤害提升
	private int toughness; // 韧性提升
	private int lifeReceive; // 生命回复提升
	private int energyRecive; // 能量回复提升
	private int energyTrans; // 能量转化提升
	private int attackSpeed; // 攻击速度提升
	private int cutHurt;
	private int resist;
	private int addCure;
	private int cutCure;

	private int lifePercent;
	private int attackPercent;
	private int physiqueDefPercent;
	private int spiritDefPercent;
	private int attackVampirePercent;
	private int criticalPercent;
	private int criticalHurtPercent;
	private int toughnessPercent;
	private int lifeReceivePercent;
	private int energyReceivePercent;
	private int energyTransPercent;
	private int attackSpeedPercent;
	private int cutHurtPercent;
	private int resistPercent;
	private int addCurePercent;
	private int cutCurePercent;

	private int composeCost;
	private int composeNeedNum;
	private int composeItemID;
	private int gemType;
	private int level;
	private int gemLevel;

	private String attrData;// 增加的固定值属性
	private String precentAttrData;// 增加的百分比属性
	private Map<Integer, Integer> attrDataMap;// 增加固定值属性
	private Map<Integer, Integer> precentAttrDataMap;// 增加的百分比属性

	public int getLife() {
		return life;
	}

	public void setLife(int hpMax) {
		this.life = hpMax;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getSpiritDef() {
		return spiritDef;
	}

	public void setSpiritDef(int magicDefense) {
		this.spiritDef = magicDefense;
	}

	public int getAttackVampire() {
		return attackVampire;
	}

	public void setAttackVampire(int genHp) {
		this.attackVampire = genHp;
	}

	public int getCritical() {
		return critical;
	}

	public void setCritical(int critPercent) {
		this.critical = critPercent;
	}

	public int getCriticalHurt() {
		return criticalHurt;
	}

	public void setCriticalHurt(int crit) {
		this.criticalHurt = crit;
	}

	public int getToughness() {
		return toughness;
	}

	public void setToughness(int rockSece) {
		this.toughness = rockSece;
	}

	public int getLifeReceive() {
		return lifeReceive;
	}

	public void setLifeReceive(int hpRevocver) {
		this.lifeReceive = hpRevocver;
	}

	public int getEnergyReceive() {
		return energyRecive;
	}

	public void setEnergyReceive(int energyRecover) {
		this.energyRecive = energyRecover;
	}

	public int getEnergyTrans() {
		return energyTrans;
	}

	public void setEnergyTrans(int enegerTramsfor) {
		this.energyTrans = enegerTramsfor;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public void setAttackSpeed(int attackSpeed) {
		this.attackSpeed = attackSpeed;
	}

	public int getComposeItemID() {
		return composeItemID;
	}

	public void setComposeItemID(int composeItemID) {
		this.composeItemID = composeItemID;
	}

	public int getComposeNeedNum() {
		return composeNeedNum;
	}

	public void setComposeNeedNum(int composeNeedNum) {
		this.composeNeedNum = composeNeedNum;
	}

	public int getComposeCost() {
		return composeCost;
	}

	public void setComposeCost(int composeCost) {
		this.composeCost = composeCost;
	}

	public int getGemType() {
		return gemType;
	}

	public void setGemType(int gemType) {
		this.gemType = gemType;
	}

	public int getResist() {
		return resist;
	}

	public void setResist(int resist) {
		this.resist = resist;
	}

	public int getAddCure() {
		return addCure;
	}

	public void setAddCure(int addCure) {
		this.addCure = addCure;
	}

	public int getCutCure() {
		return cutCure;
	}

	public void setCutCure(int cutCure) {
		this.cutCure = cutCure;
	}

	public int getCutHurt() {
		return cutHurt;
	}

	public void setCutHurt(int cutHurt) {
		this.cutHurt = cutHurt;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getEnergyRecive() {
		return energyRecive;
	}

	public void setEnergyRecive(int energyRecive) {
		this.energyRecive = energyRecive;
	}

	public int getLifePercent() {
		return lifePercent;
	}

	public void setLifePercent(int lifePercent) {
		this.lifePercent = lifePercent;
	}

	public int getAttackPercent() {
		return attackPercent;
	}

	public void setAttackPercent(int attackPercent) {
		this.attackPercent = attackPercent;
	}

	public int getSpiritDefPercent() {
		return spiritDefPercent;
	}

	public void setSpiritDefPercent(int spiritDefPercent) {
		this.spiritDefPercent = spiritDefPercent;
	}

	public int getAttackVampirePercent() {
		return attackVampirePercent;
	}

	public void setAttackVampirePercent(int attackVampirePercent) {
		this.attackVampirePercent = attackVampirePercent;
	}

	public int getCriticalPercent() {
		return criticalPercent;
	}

	public void setCriticalPercent(int criticalPercent) {
		this.criticalPercent = criticalPercent;
	}

	public int getCriticalHurtPercent() {
		return criticalHurtPercent;
	}

	public void setCriticalHurtPercent(int criticalHurtPercent) {
		this.criticalHurtPercent = criticalHurtPercent;
	}

	public int getToughnessPercent() {
		return toughnessPercent;
	}

	public void setToughnessPercent(int toughnessPercent) {
		this.toughnessPercent = toughnessPercent;
	}

	public int getLifeReceivePercent() {
		return lifeReceivePercent;
	}

	public void setLifeReceivePercent(int lifeReceivePercent) {
		this.lifeReceivePercent = lifeReceivePercent;
	}

	public int getEnergyReceivePercent() {
		return energyReceivePercent;
	}

	public void setEnergyReceivePercent(int energyReceivePercent) {
		this.energyReceivePercent = energyReceivePercent;
	}

	public int getEnergyTransPercent() {
		return energyTransPercent;
	}

	public void setEnergyTransPercent(int energyTransPercent) {
		this.energyTransPercent = energyTransPercent;
	}

	public int getAttackSpeedPercent() {
		return attackSpeedPercent;
	}

	public void setAttackSpeedPercent(int attackSpeedPercent) {
		this.attackSpeedPercent = attackSpeedPercent;
	}

	public int getCutHurtPercent() {
		return cutHurtPercent;
	}

	public void setCutHurtPercent(int cutHurtPercent) {
		this.cutHurtPercent = cutHurtPercent;
	}

	public int getResistPercent() {
		return resistPercent;
	}

	public void setResistPercent(int resistPercent) {
		this.resistPercent = resistPercent;
	}

	public int getAddCurePercent() {
		return addCurePercent;
	}

	public void setAddCurePercent(int addCurePercent) {
		this.addCurePercent = addCurePercent;
	}

	public int getCutCurePercent() {
		return cutCurePercent;
	}

	public void setCutCurePercent(int cutCurePercent) {
		this.cutCurePercent = cutCurePercent;
	}

	public int getPhysiqueDef() {
		return physiqueDef;
	}

	public void setPhysiqueDef(int physiqueDef) {
		this.physiqueDef = physiqueDef;
	}

	public int getPhysiqueDefPercent() {
		return physiqueDefPercent;
	}

	public void setPhysiqueDefPercent(int physiqueDefPercent) {
		this.physiqueDefPercent = physiqueDefPercent;
	}

	public int getGemLevel() {
		return gemLevel;
	}

	public void setGemLevel(int gemLevel) {
		this.gemLevel = gemLevel;
	}

	/**
	 * <pre>
	 * 获取增加的固定值属性
	 * 返回的这个Map的key是{@link AttributeType}的属性类型
	 * 返回的value（都是放大到了{@link AttributeConst#DIVISION}的倍数）有特殊处理，计算属性全部是用的int类型，然而为了防止
	 * 配置中会出现float类型的数据，所有这里凡是遇到在{@link AttrData}
	 * 中字段是float类型的的属性，都会把配置中的值扩大{@link AttributeConst#BIG_FLOAT}
	 * 的倍数
	 * </pre>
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getAttrDataMap() {
		return attrDataMap;
	}

	/**
	 * <pre>
	 * 获取增加的百分比属性
	 * 返回的这个Map的key是{@link AttributeType}的属性类型
	 * 返回的value（都是放大到了{@link AttributeConst#DIVISION}的倍数）有特殊处理，计算属性全部是用的int类型，然而为了防止
	 * 配置中会出现float类型的数据，所有这里凡是遇到在{@link AttrData}
	 * 中字段是float类型的的属性，都会把配置中的值扩大{@link AttributeConst#BIG_FLOAT}
	 * 的倍数
	 * </pre>
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getPrecentAttrDataMap() {
		return precentAttrDataMap;
	}

	/**
	 * 初始化解析属性
	 */
	public void initData() {
		// ===============================增加的固定属性
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map(attrData);
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map(precentAttrData);
	}
}