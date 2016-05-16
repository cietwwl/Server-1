package com.rwbase.dao.role.pojo;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleQualityCfg {
	private String id;
	private String nextId;// 下一品阶ID...
	private int quality;// 品阶...
	private String qualityName;// 品阶名字...
	private int equip1;// 装备1...
	private int equip2;// 装备2...
	private int equip3;// 装备3...
	private int equip4;// 装备4...
	private int equip5;// 装备5...
	private int equip6;// 装备6...
	private String frameColor;// 头像框颜色...
	private String careerName; // 职业名称
	private String qualityBg;
	private String careerIcon;
	private String playerQualityBg; // 主角品质框

	// 属性相关
	private int life; // 最大生命值...
	private int energy; // 能量值...
	private int attack; // 攻击...
	private int physiqueDef; // 体魄防御...
	private int spiritDef; // 精神防御...
	private int attackVampire; // 攻击吸血...
	private int critical; // 暴击率...
	private int criticalHurt; // 暴击伤害提升...
	private int toughness; // 韧性...
	private int lifeReceive; // 生命回复...
	private int energyReceive; // 能量值回复...
	private int struckEnergy;// 击杀增加能量...
	private int attackEnergy;// 攻击能量...
	private int energyTrans; // 能量转化...
	private int cutHurt;// 伤害减免
	private int cutCritHurt;// 暴击伤害减免
	private int resist;// 抵抗
	private int addCure;// 受到治疗效果增加
	private int cutCure;// 受到治疗效果减少
	private int lifeGrowUp; // 生命成长...
	private int attackGrowUp; // 攻击成长...
	private int physicqueDefGrowUp; // 体魄防御成长...
	private int spiritDefGrowUp; // 精神防御成长...
	private int enchantExp;// 附灵经验...
	private int skillLevel;// 技能总等级
	private int attackType;// 攻击类型
	private int dodge; // 闪避
	private int hit; // 命中
	private int energyPerSecond; // 每秒恢复的能量

	private float hardStraight;
	private float reactionTime;
	private float attackDistance; // 攻击距离...
	private float attackSpeed; // 攻击速度...
	private float moveSpeed; // 移动速度...
	private float attackHurt;// 攻击伤害
	private float viewRange; // 视野范围

	private float volumeRadius; // 人物半径

	private float doHurt; // 硬直界限

	private String attrData;// 增加的固定值属性
	private String precentAttrData;// 增加的百分比属性
	private Map<Integer, Integer> attrDataMap;// 增加固定值属性
	private Map<Integer, Integer> precentAttrDataMap;// 增加的百分比属性

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNextId() {
		return nextId;
	}

	public void setNextId(String nextId) {
		this.nextId = nextId;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public String getQualityName() {
		return qualityName;
	}

	public void setQualityName(String qualityName) {
		this.qualityName = qualityName;
	}

	public String getFrameColor() {
		return frameColor;
	}

	public void setFrameColor(String frameColor) {
		this.frameColor = frameColor;
	}

	public float getLife() {
		return life;
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

	public void setSpiritDef(int spiritDef) {
		this.spiritDef = spiritDef;
	}

	public int getCritical() {
		return critical;
	}

	public void setCritical(int critical) {
		this.critical = critical;
	}

	public int getCriticalHurt() {
		return criticalHurt;
	}

	public void setCriticalHurt(int criticalHurt) {
		this.criticalHurt = criticalHurt;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public String getQualityBg() {
		return qualityBg;
	}

	public void setQualityBg(String qualityBg) {
		this.qualityBg = qualityBg;
	}

	public int getEquip1() {
		return equip1;
	}

	public void setEquip1(int equip1) {
		this.equip1 = equip1;
	}

	public int getEquip2() {
		return equip2;
	}

	public void setEquip2(int equip2) {
		this.equip2 = equip2;
	}

	public int getEquip3() {
		return equip3;
	}

	public void setEquip3(int equip3) {
		this.equip3 = equip3;
	}

	public int getEquip4() {
		return equip4;
	}

	public void setEquip4(int equip4) {
		this.equip4 = equip4;
	}

	public int getEquip5() {
		return equip5;
	}

	public void setEquip5(int equip5) {
		this.equip5 = equip5;
	}

	public int getEquip6() {
		return equip6;
	}

	public void setEquip6(int equip6) {
		this.equip6 = equip6;
	}

	public String getCareerName() {
		return careerName;
	}

	public void setCareerName(String careerName) {
		this.careerName = careerName;
	}

	public String getCareerIcon() {
		return careerIcon;
	}

	public void setCareerIcon(String careerIcon) {
		this.careerIcon = careerIcon;
	}

	public String getPlayerQualityBg() {
		return playerQualityBg;
	}

	public void setPlayerQualityBg(String playerQualityBg) {
		this.playerQualityBg = playerQualityBg;
	}

	public int getLifeReceive() {
		return lifeReceive;
	}

	public void setLifeReceive(int lifeReceive) {
		this.lifeReceive = lifeReceive;
	}

	public int getEnergyReceive() {
		return energyReceive;
	}

	public void setEnergyReceive(int energyReceive) {
		this.energyReceive = energyReceive;
	}

	public int getToughness() {
		return toughness;
	}

	public void setToughness(int toughness) {
		this.toughness = toughness;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getPhysiqueDef() {
		return physiqueDef;
	}

	public void setPhysiqueDef(int physiqueDef) {
		this.physiqueDef = physiqueDef;
	}

	public int getAttackVampire() {
		return attackVampire;
	}

	public void setAttackVampire(int attackVampire) {
		this.attackVampire = attackVampire;
	}

	public int getStruckEnergy() {
		return struckEnergy;
	}

	public void setStruckEnergy(int struckEnergy) {
		this.struckEnergy = struckEnergy;
	}

	public int getAttackEnergy() {
		return attackEnergy;
	}

	public void setAttackEnergy(int attackEnergy) {
		this.attackEnergy = attackEnergy;
	}

	public int getEnergyTrans() {
		return energyTrans;
	}

	public void setEnergyTrans(int energyTrans) {
		this.energyTrans = energyTrans;
	}

	public int getCutHurt() {
		return cutHurt;
	}

	public void setCutHurt(int cutHurt) {
		this.cutHurt = cutHurt;
	}

	public int getCutCritHurt() {
		return cutCritHurt;
	}

	public void setCutCritHurt(int cutCritHurt) {
		this.cutCritHurt = cutCritHurt;
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

	public int getPhysicqueDefGrowUp() {
		return physicqueDefGrowUp;
	}

	public void setPhysicqueDefGrowUp(int physicqueDefGrowUp) {
		this.physicqueDefGrowUp = physicqueDefGrowUp;
	}

	public int getSpiritDefGrowUp() {
		return spiritDefGrowUp;
	}

	public void setSpiritDefGrowUp(int spiritDefGrowUp) {
		this.spiritDefGrowUp = spiritDefGrowUp;
	}

	public int getEnchantExp() {
		return enchantExp;
	}

	public void setEnchantExp(int enchantExp) {
		this.enchantExp = enchantExp;
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}

	public int getAttackType() {
		return attackType;
	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public int getDodge() {
		return dodge;
	}

	public void setDodge(int dodge) {
		this.dodge = dodge;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int getEnergyPerSecond() {
		return energyPerSecond;
	}

	public void setEnergyPerSecond(int energyPerSecond) {
		this.energyPerSecond = energyPerSecond;
	}

	public float getHardStraight() {
		return hardStraight;
	}

	public void setHardStraight(float hardStraight) {
		this.hardStraight = hardStraight;
	}

	public float getReactionTime() {
		return reactionTime;
	}

	public void setReactionTime(float reactionTime) {
		this.reactionTime = reactionTime;
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

	public float getAttackHurt() {
		return attackHurt;
	}

	public void setAttackHurt(float attackHurt) {
		this.attackHurt = attackHurt;
	}

	public float getViewRange() {
		return viewRange;
	}

	public void setViewRange(float viewRange) {
		this.viewRange = viewRange;
	}

	public float getVolumeRadius() {
		return volumeRadius;
	}

	public void setVolumeRadius(float volumeRadius) {
		this.volumeRadius = volumeRadius;
	}

	public float getDoHurt() {
		return doHurt;
	}

	public void setDoHurt(float doHurt) {
		this.doHurt = doHurt;
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
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map("RoleQualityCfg", attrData);
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map("RoleQualityCfg", precentAttrData);
	}
}
