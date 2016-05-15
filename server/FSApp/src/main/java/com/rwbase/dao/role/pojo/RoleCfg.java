package com.rwbase.dao.role.pojo;

import java.util.Collections;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.util.StringUtils;

import com.common.HPCUtil;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleCfg {

	private String roleId;// 佣兵ID
	private String nextRoleId;// 下一级佣兵ID

	private String name;// 名字
	private String atlas;// 图集
	private String imageId;// 头像ID
	private String heroDesc;// 英雄介绍
	private String heroIllustrate;// 英雄说明
	private String goodAt;// 擅长
	private int sex;
	private String bustImage;

	private String qualityId;// 品阶ID
	private String battleIcon;
	private String title;// 称号
	private String nameIcon;// 名字图标
	private String attackId;// 普攻ID
	private String voice;// 声音
	private String titleIcon;// 称号图标
	private String fullEnergyIcon;// 怒气满头像
	private String soundName;
	private String actionName;

	private int modelId;// 模型ID
	private int careerType;// 职业
	private int order;// 站位
	private int group;// 组合
	private int starLevel;// 星级
	private int roleType;
	private int soulStoneId;// 灵魂石ID
	private int summonNumber;// 召唤数量
	private int risingNumber;// 升星数量
	private int transform;// 转换数量
	private int upNeedCoin;// 升星铜钱
	private int summonFlag;// 召唤标志
	private int canShow; // 是否在佣兵列表中显示佣兵
	private int camp;

	// 以下是战斗属性
	private int hit;// 命中
	private int dodge;// 闪避
	private int lifeGrowUp; // 生命成长...
	private int attackGrowUp; // 攻击成长...
	private int physicqueDefGrowUp; // 体魄防御成长...
	private int spiritDefGrowUp; // 精神防御成长...
	private int life; // 基础生命值...
	private int energy; // 基础能量值...
	private int attack; // 基础攻击...
	private int physiqueDef; // 基础体魄防御...
	private int spiritDef; // 基础精神防御...
	private int attackVampire; // 基础攻击吸血...
	private int critical; // 暴击率...
	private int criticalHurt; // 暴击伤害提升...
	private int toughness; // 韧性...
	private int lifeReceive; // 生命回复...
	private int energyReceive; // 能量回复...
	private int energyTrans; // 能量转化...
	private int cutHurt;// 伤害减免
	private int cutCritHurt;// 暴击伤害减免
	private int resist;// 抵抗
	private int addCure;// 收到治疗效果增加
	private int cutCure;// 收到治疗效果降低
	private int attackEnergy;// 攻击增加能量
	private int struckEnergy;// 击杀增加能量
	private int energyPerSecond; // 每秒恢复的能量
	private int attackType;// 攻击类型

	private float hardStraight;
	private float reactionTime;// 反应时间
	private float attackDistance; // 攻击距离...
	private float attackSpeed; // 攻击速度...
	private float moveSpeed; // 移动速度...
	private float attackHurt;// 伤害增加百分比
	private float volumeRadius; // 人物半径
	private float doHurt; // 硬直界限
	private float viewRange; // 视野范围

	private String skillId01;// 技能1
	private String skillId02;// 技能2
	private String skillId03;// 技能3
	private String skillId04;// 技能4
	private String skillId05;// 技能5
	private String dieSkillId;// 死亡技能

	private String growUp;// 成长率
	private String attrData;// 增加的固定值属性
	private String precentAttrData;// 增加的百分比属性
	private Map<Integer, Integer> growUpMap;// 成长率
	private Map<Integer, Integer> attrDataMap;// 增加固定值属性
	private Map<Integer, Integer> precentAttrDataMap;// 增加的百分比属性

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getNextRoleId() {
		return nextRoleId;
	}

	public void setNextRoleId(String nextRoleId) {
		this.nextRoleId = nextRoleId;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAtlas() {
		return atlas;
	}

	public void setAtlas(String atlas) {
		this.atlas = atlas;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public int getCareerType() {
		return careerType;
	}

	public void setCareerType(int careerType) {
		this.careerType = careerType;
	}

	public String getHeroDesc() {
		return heroDesc;
	}

	public void setHeroDesc(String heroDesc) {
		this.heroDesc = heroDesc;
	}

	public String getHeroIllustrate() {
		return heroIllustrate;
	}

	public void setHeroIllustrate(String heroIllustrate) {
		this.heroIllustrate = heroIllustrate;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(int starLevel) {
		this.starLevel = starLevel;
	}

	public String getGoodAt() {
		return goodAt;
	}

	public void setGoodAt(String goodAt) {
		this.goodAt = goodAt;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getBustImage() {
		return bustImage;
	}

	public void setBustImage(String bustImage) {
		this.bustImage = bustImage;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int getDodge() {
		return dodge;
	}

	public void setDodge(int dodge) {
		this.dodge = dodge;
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

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
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

	public int getToughness() {
		return toughness;
	}

	public void setToughness(int toughness) {
		this.toughness = toughness;
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

	public int getAttackEnergy() {
		return attackEnergy;
	}

	public void setAttackEnergy(int attackEnergy) {
		this.attackEnergy = attackEnergy;
	}

	public int getStruckEnergy() {
		return struckEnergy;
	}

	public void setStruckEnergy(int struckEnergy) {
		this.struckEnergy = struckEnergy;
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

	public int getAttackType() {
		return attackType;
	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public String getSkillId01() {
		return skillId01;
	}

	public void setSkillId01(String skillId01) {
		this.skillId01 = skillId01;
	}

	public String getSkillId02() {
		return skillId02;
	}

	public void setSkillId02(String skillId02) {
		this.skillId02 = skillId02;
	}

	public String getSkillId03() {
		return skillId03;
	}

	public void setSkillId03(String skillId03) {
		this.skillId03 = skillId03;
	}

	public String getSkillId04() {
		return skillId04;
	}

	public void setSkillId04(String skillId04) {
		this.skillId04 = skillId04;
	}

	public String getSkillId05() {
		return skillId05;
	}

	public void setSkillId05(String skillId05) {
		this.skillId05 = skillId05;
	}

	public String getDieSkillId() {
		return dieSkillId;
	}

	public void setDieSkillId(String dieSkillId) {
		this.dieSkillId = dieSkillId;
	}

	public String getQualityId() {
		return qualityId;
	}

	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}

	public String getBattleIcon() {
		return battleIcon;
	}

	public void setBattleIcon(String battleIcon) {
		this.battleIcon = battleIcon;
	}

	public int getSoulStoneId() {
		return soulStoneId;
	}

	public void setSoulStoneId(int soulStoneId) {
		this.soulStoneId = soulStoneId;
	}

	public int getSummonNumber() {
		return summonNumber;
	}

	public void setSummonNumber(int summonNumber) {
		this.summonNumber = summonNumber;
	}

	public int getRisingNumber() {
		return risingNumber;
	}

	public void setRisingNumber(int risingNumber) {
		this.risingNumber = risingNumber;
	}

	public int getTransform() {
		return transform;
	}

	public void setTransform(int transform) {
		this.transform = transform;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNameIcon() {
		return nameIcon;
	}

	public void setNameIcon(String nameIcon) {
		this.nameIcon = nameIcon;
	}

	public int getUpNeedCoin() {
		return upNeedCoin;
	}

	public void setUpNeedCoin(int upNeedCoin) {
		this.upNeedCoin = upNeedCoin;
	}

	public int getSummonFlag() {
		return summonFlag;
	}

	public void setSummonFlag(int summonFlag) {
		this.summonFlag = summonFlag;
	}

	public String getAttackId() {
		return attackId;
	}

	public void setAttackId(String attackId) {
		this.attackId = attackId;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getTitleIcon() {
		return titleIcon;
	}

	public void setTitleIcon(String titleIcon) {
		this.titleIcon = titleIcon;
	}

	public String getFullEnergyIcon() {
		return fullEnergyIcon;
	}

	public void setFullEnergyIcon(String fullEnergyIcon) {
		this.fullEnergyIcon = fullEnergyIcon;
	}

	public String getSoundName() {
		return soundName;
	}

	public void setSoundName(String soundName) {
		this.soundName = soundName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public int getCanShow() {
		return canShow;
	}

	public void setCanShow(int canShow) {
		this.canShow = canShow;
	}

	public int getCamp() {
		return camp;
	}

	public void setCamp(int camp) {
		this.camp = camp;
	}

	public int getEnergyPerSecond() {
		return energyPerSecond;
	}

	public void setEnergyPerSecond(int energyPerSecond) {
		this.energyPerSecond = energyPerSecond;
	}

	public float getViewRange() {
		return viewRange;
	}

	public void setViewRange(float viewRange) {
		this.viewRange = viewRange;
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
	 * 获取成长率的集合
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getGrowUpMap() {
		return growUpMap;
	}

	/**
	 * 初始化解析属性
	 */
	public void initData() {
		// 计算成长率
		if (StringUtils.isEmpty(growUp)) {
			this.growUpMap = Collections.emptyMap();
		} else {
			this.growUpMap = AttributeUtils.parseAttrDataStr2Map(growUp);
		}
		// ===============================增加的固定属性
		this.attrDataMap = AttributeUtils.parseAttrDataStr2Map(attrData);
		// ===============================增加的百分比属性
		this.precentAttrDataMap = AttributeUtils.parseAttrDataStr2Map(precentAttrData);
	}
}