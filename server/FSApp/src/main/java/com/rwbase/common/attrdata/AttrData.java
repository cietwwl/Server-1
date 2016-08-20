package com.rwbase.common.attrdata;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.common.BeanOperationHelper;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rwbase.common.attribute.AttributeConst;

@SynClass
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class AttrData implements AttrDataIF {

	@IgnoreSynField
	private static final String _KEY_LIFE = "1";
	@IgnoreSynField
	private static final String _KEY_ENERGY = "2";
	@IgnoreSynField
	private static final String _KEY_PHYSIQUE_ATTACK = "3";
	@IgnoreSynField
	private static final String _KEY_SPIRIT_ATTACK = "4";
	@IgnoreSynField
	private static final String _KEY_PHYSIQUE_DEF = "5";
	@IgnoreSynField
	private static final String _KEY_SPIRIT_DEF = "6";
	@IgnoreSynField
	private static final String _KEY_ATTACK_VAMPIRE = "7";
	@IgnoreSynField
	private static final String _KEY_CRITICAL = "8";
	@IgnoreSynField
	private static final String _KEY_CRITICAL_HURT = "9";
	@IgnoreSynField
	private static final String _KEY_TOUGHNESS = "10";
	@IgnoreSynField
	private static final String _KEY_LIFE_RECEIVE = "11";
	@IgnoreSynField
	private static final String _KEY_ENERGY_RECEIVE = "12";
	@IgnoreSynField
	private static final String _KEY_STRUCK_ENERGY = "13";
	@IgnoreSynField
	private static final String _KEY_ATTACK_ENERGY = "14";
	@IgnoreSynField
	private static final String _KEY_ENERGY_TRANS = "15";
	@IgnoreSynField
	private static final String _KEY_CUT_HURT = "16";
	@IgnoreSynField
	private static final String _KEY_CUT_CRIT_HURT = "17";
	@IgnoreSynField
	private static final String _KEY_RESIST = "18";
	@IgnoreSynField
	private static final String _KEY_ADD_CURE = "19";
	@IgnoreSynField
	private static final String _KEY_CUT_CURE = "20";
	@IgnoreSynField
	private static final String _KEY_LIFE_GROW_UP = "21";
	@IgnoreSynField
	private static final String _KEY_P_ATTACK_GROW_UP = "22";
	@IgnoreSynField
	private static final String _KEY_S_ATTACK_GROW_UP = "23";
	@IgnoreSynField
	private static final String _KEY_PHYSICQUE_DEF_GROW_UP = "24";
	@IgnoreSynField
	private static final String _KEY_SPIRIT_DEF_GROW_UP = "25";
	@IgnoreSynField
	private static final String _KEY_ENCHANT_EXP = "26";
	@IgnoreSynField
	private static final String _KEY_SKILL_LEVEL = "27";
	@IgnoreSynField
	private static final String _KEY_ATTACK_TYPE = "28";
	@IgnoreSynField
	private static final String _KEY_DODGE = "29";
	@IgnoreSynField
	private static final String _KEY_HIT = "30";
	@IgnoreSynField
	private static final String _KEY_ENERGY_PER_SECOND = "31";
	@IgnoreSynField
	private static final String _KEY_HARD_STRAIGHT = "32";
	@IgnoreSynField
	private static final String _KEY_REACTION_TIME = "33";
	@IgnoreSynField
	private static final String _KEY_ATTACK_DISTANCE = "34";
	@IgnoreSynField
	private static final String _KEY_ATTACK_SPEED = "35";
	@IgnoreSynField
	private static final String _KEY_MOVE_SPEED = "36";
	@IgnoreSynField
	private static final String _KEY_ATTACK_HURT = "37";
	@IgnoreSynField
	private static final String _KEY_VIEW_RANGE = "38";
	@IgnoreSynField
	private static final String _KEY_VOLUME_RADIUS = "39";
	@IgnoreSynField
	private static final String _KEY_DO_HURT = "40";
	
	@JsonProperty(_KEY_LIFE)
	private int life; // 最大生命值...
	
	@JsonProperty(_KEY_ENERGY)
	private int energy; // 能量值...
	
	@JsonProperty(_KEY_PHYSIQUE_ATTACK)
	private int physiqueAttack; // 物理攻击...
	
	@JsonProperty(_KEY_SPIRIT_ATTACK)
	private int spiritAttack;// 魔法攻击...
	
	@JsonProperty(_KEY_PHYSIQUE_DEF)
	private int physiqueDef; // 体魄防御...
	
	@JsonProperty(_KEY_SPIRIT_DEF)
	private int spiritDef; // 精神防御...
	
	@JsonProperty(_KEY_ATTACK_VAMPIRE)
	private int attackVampire; // 攻击吸血...
	
	@JsonProperty(_KEY_CRITICAL)
	private int critical; // 暴击率...
	
	@JsonProperty(_KEY_CRITICAL_HURT)
	private int criticalHurt; // 暴击伤害提升...
	
	@JsonProperty(_KEY_TOUGHNESS)
	private int toughness; // 韧性...
	
	@JsonProperty(_KEY_LIFE_RECEIVE)
	private int lifeReceive; // 生命回复...
	
	@JsonProperty(_KEY_ENERGY_RECEIVE)
	private int energyReceive; // 能量值回复...
	
	@JsonProperty(_KEY_STRUCK_ENERGY)
	private int struckEnergy;// 击杀增加能量...
	
	@JsonProperty(_KEY_ATTACK_ENERGY)
	private int attackEnergy;// 攻击能量...
	
	@JsonProperty(_KEY_ENERGY_TRANS)
	private int energyTrans; // 能量转化...
	
	@JsonProperty(_KEY_CUT_HURT)
	private int cutHurt;// 伤害减免
	
	@JsonProperty(_KEY_CUT_CRIT_HURT)
	private int cutCritHurt;// 暴击伤害减免
	
	@JsonProperty(_KEY_RESIST)
	private int resist;// 抵抗
	
	@JsonProperty(_KEY_ADD_CURE)
	private int addCure;// 受到治疗效果增加
	
	@JsonProperty(_KEY_CUT_CURE)
	private int cutCure;// 受到治疗效果减少
	
	@JsonProperty(_KEY_LIFE_GROW_UP)
	private int lifeGrowUp; // 生命成长...
	
	@JsonProperty(_KEY_P_ATTACK_GROW_UP)
	private int pAttackGrowUp; // 物理攻击成长...
	
	@JsonProperty(_KEY_S_ATTACK_GROW_UP)
	private int sAttackGrowUp; // 法术攻击成长...
	
	@JsonProperty(_KEY_PHYSICQUE_DEF_GROW_UP)
	private int physicqueDefGrowUp; // 体魄防御成长...
	
	@JsonProperty(_KEY_SPIRIT_DEF_GROW_UP)
	private int spiritDefGrowUp; // 精神防御成长...
	
	@JsonProperty(_KEY_ENCHANT_EXP)
	private int enchantExp;// 附灵经验...
	
	@JsonProperty(_KEY_SKILL_LEVEL)
	private int skillLevel;// 技能总等级
	
	@JsonProperty(_KEY_ATTACK_TYPE)
	private int attackType;// 攻击类型
	
	@JsonProperty(_KEY_DODGE)
	private int dodge; // 闪避
	
	@JsonProperty(_KEY_HIT)
	private int hit; // 命中
	
	@JsonProperty(_KEY_ENERGY_PER_SECOND)
	private int energyPerSecond; // 每秒恢复的能量

	@JsonProperty(_KEY_HARD_STRAIGHT)
	private float hardStraight;
	
	@JsonProperty(_KEY_REACTION_TIME)
	private float reactionTime;
	
	@JsonProperty(_KEY_ATTACK_DISTANCE)
	private float attackDistance; // 攻击距离...
	
	@JsonProperty(_KEY_ATTACK_SPEED)
	private float attackSpeed; // 攻击速度...
	
	@JsonProperty(_KEY_MOVE_SPEED)
	private float moveSpeed; // 移动速度...
	
	@JsonProperty(_KEY_ATTACK_HURT)
	private float attackHurt;// 攻击伤害
	
	@JsonProperty(_KEY_VIEW_RANGE)
	private float viewRange; // 视野范围
	
	@JsonProperty(_KEY_VOLUME_RADIUS)
	private float volumeRadius; // 人物半径
	
	@JsonProperty(_KEY_DO_HURT)
	private float doHurt; // 硬直界限
	
	public AttrData() {}

	public AttrData(int life, int energy, int physiqueAttack, int spiritAttack, int physiqueDef, int spiritDef, int attackVampire, int critical, int criticalHurt, int toughness, int lifeReceive,
			int energyReceive, int struckEnergy, int attackEnergy, int energyTrans, int cutHurt, int cutCritHurt, int resist, int addCure, int cutCure, int lifeGrowUp, int pAttackGrowUp,
			int sAttackGrowUp, int physicqueDefGrowUp, int spiritDefGrowUp, int enchantExp, int skillLevel, int attackType, int dodge, int hit, int energyPerSecond, float hardStraight,
			float reactionTime, float attackDistance, float attackSpeed, float moveSpeed, float attackHurt, float viewRange, float volumeRadius, float doHurt) {
		this.life = life;
		this.energy = energy;
		this.physiqueAttack = physiqueAttack;
		this.spiritAttack = spiritAttack;
		this.physiqueDef = physiqueDef;
		this.spiritDef = spiritDef;
		this.attackVampire = attackVampire;
		this.critical = critical;
		this.criticalHurt = criticalHurt;
		this.toughness = toughness;
		this.lifeReceive = lifeReceive;
		this.energyReceive = energyReceive;
		this.struckEnergy = struckEnergy;
		this.attackEnergy = attackEnergy;
		this.energyTrans = energyTrans;
		this.cutHurt = cutHurt;
		this.cutCritHurt = cutCritHurt;
		this.resist = resist;
		this.addCure = addCure;
		this.cutCure = cutCure;
		this.lifeGrowUp = lifeGrowUp;
		this.pAttackGrowUp = pAttackGrowUp;
		this.sAttackGrowUp = sAttackGrowUp;
		this.physicqueDefGrowUp = physicqueDefGrowUp;
		this.spiritDefGrowUp = spiritDefGrowUp;
		this.enchantExp = enchantExp;
		this.skillLevel = skillLevel;
		this.attackType = attackType;
		this.dodge = dodge;
		this.hit = hit;
		this.energyPerSecond = energyPerSecond;
		this.hardStraight = hardStraight;
		this.reactionTime = reactionTime;
		this.attackDistance = attackDistance;
		this.attackSpeed = attackSpeed;
		this.moveSpeed = moveSpeed;
		this.attackHurt = attackHurt;
		this.viewRange = viewRange;
		this.volumeRadius = volumeRadius;
		this.doHurt = doHurt;
	}

	public AttrData addPercent(AttrData target) {
		if (target == null) {
			return this;
		}
		BeanOperationHelper.addPercentObject(this, target, AttributeConst.DIVISION);
		return this;
	}

	public AttrData addPercent(int mutiNumber) {
		BeanOperationHelper.addPercent(this, mutiNumber, AttributeConst.DIVISION);
		return this;
	}

	public AttrData plus(AttrDataIF target) {
		if (target == null) {
			return this;
		}
		BeanOperationHelper.plus(this, target);
		return this;
	}

	public static String getLog(AttrData source) {
		if (source == null) {
			return null;
		}
		return BeanOperationHelper.getPositiveValueDiscription(source);
	}

	// public static AttrData fromObject(Object source) {
	// AttrData data = new AttrData();
	// BeanCopyer.copy(source, data);
	// return data;
	// }
	//
	// public static AttrData fromPercentObject(Object source) {
	// AttrData data = new AttrData();
	// BeanCopyer.copy(source, data, new IBeanNameFixAction() {
	// @Override
	// public String doFix(String name) {
	// return StringUtils.substringBefore(name, "Percent");
	// }
	// });
	// return data;
	// }
	//
	// public static AttrData fromPercentObjectToAttrData(Object source) {
	// AttrData data = new AttrData();
	// BeanCopyer.copyFormPercentObject(source, data, new IBeanNameFixAction() {
	// @Override
	// public String doFix(String name) {
	// return StringUtils.substringBefore(name, "Percent");
	// }
	// });
	// return data;
	// }
	//
	// /**
	// * 根据配置表字符串加入值
	// *
	// * @param cfgStr example: life:30,attack:40
	// * @return
	// */
	// public static AttrData fromCfgStr(String cfgStr) {
	// AttrData data = null;
	// final String Attr_Split = ",";
	// final String File_Value_Split = ":";
	// String[] attrCfg = cfgStr.split(Attr_Split);
	// Map<String, String> attrMap = new HashMap<String, String>();
	// for (String attrValueTmp : attrCfg) {
	// String[] split = attrValueTmp.trim().split(File_Value_Split);
	// if (split.length == 2) {
	// String filedName = split[0];
	// String filedValue = split[1];
	// attrMap.put(filedName, filedValue);
	// }
	// }
	// if (attrMap.size() > 0) {
	// data = fromMap(attrMap);
	// }
	// return data;
	// }
	//
	// private static AttrData fromMap(Map<String, String> mapData) {
	// AttrData data = new AttrData();
	// BeanOperationHelper.plus(data, mapData);
	// return data;
	// }

	public int getLife() {
		return life;
	}

	public int getEnergy() {
		return energy;
	}

	public int getPhysiqueAttack() {
		return physiqueAttack;
	}

	public int getSpiritAttack() {
		return spiritAttack;
	}

	public int getPhysiqueDef() {
		return physiqueDef;
	}

	public int getSpiritDef() {
		return spiritDef;
	}

	public int getAttackVampire() {
		return attackVampire;
	}

	public int getCritical() {
		return critical;
	}

	public int getCriticalHurt() {
		return criticalHurt;
	}

	public int getToughness() {
		return toughness;
	}

	public int getLifeReceive() {
		return lifeReceive;
	}

	public int getEnergyReceive() {
		return energyReceive;
	}

	public int getStruckEnergy() {
		return struckEnergy;
	}

	public int getAttackEnergy() {
		return attackEnergy;
	}

	public int getEnergyTrans() {
		return energyTrans;
	}

	public int getCutHurt() {
		return cutHurt;
	}

	public int getCutCritHurt() {
		return cutCritHurt;
	}

	public int getResist() {
		return resist;
	}

	public int getAddCure() {
		return addCure;
	}

	public int getCutCure() {
		return cutCure;
	}

	public int getLifeGrowUp() {
		return lifeGrowUp;
	}

	public int getpAttackGrowUp() {
		return pAttackGrowUp;
	}

	public int getSAttackGrowUp() {
		return sAttackGrowUp;
	}

	public int getPhysicqueDefGrowUp() {
		return physicqueDefGrowUp;
	}

	public int getSpiritDefGrowUp() {
		return spiritDefGrowUp;
	}

	public int getEnchantExp() {
		return enchantExp;
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public int getAttackType() {
		return attackType;
	}

	public int getDodge() {
		return dodge;
	}

	public int getHit() {
		return hit;
	}

	public int getEnergyPerSecond() {
		return energyPerSecond;
	}

	public float getHardStraight() {
		return hardStraight;
	}

	public float getReactionTime() {
		return reactionTime;
	}

	public float getAttackDistance() {
		return attackDistance;
	}

	public float getAttackSpeed() {
		return attackSpeed;
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public float getAttackHurt() {
		return attackHurt;
	}

	public float getViewRange() {
		return viewRange;
	}

	public float getVolumeRadius() {
		return volumeRadius;
	}

	public float getDoHurt() {
		return doHurt;
	}

	//
	// public void setLife(int life) {
	// this.life = life;
	// }
	//
	// public void setEnergy(int energy) {
	// this.energy = energy;
	// }
	//
	// public void setPhysiqueDef(int physiqueDef) {
	// this.physiqueDef = physiqueDef;
	// }
	//
	// public void setSpiritDef(int spiritDef) {
	// this.spiritDef = spiritDef;
	// }
	//
	// public void setAttackVampire(int attackVampire) {
	// this.attackVampire = attackVampire;
	// }
	//
	// public void setCritical(int critical) {
	// this.critical = critical;
	// }
	//
	// public void setCriticalHurt(int criticalHurt) {
	// this.criticalHurt = criticalHurt;
	// }
	//
	// public void setToughness(int toughness) {
	// this.toughness = toughness;
	// }
	//
	// public void setLifeReceive(int lifeReceive) {
	// this.lifeReceive = lifeReceive;
	// }
	//
	// public void setEnergyReceive(int energyReceive) {
	// this.energyReceive = energyReceive;
	// }
	//
	// public void setStruckEnergy(int struckEnergy) {
	// this.struckEnergy = struckEnergy;
	// }
	//
	// public void setAttackEnergy(int attackEnergy) {
	// this.attackEnergy = attackEnergy;
	// }
	//
	// public void setEnergyTrans(int energyTrans) {
	// this.energyTrans = energyTrans;
	// }
	//
	// public void setCutHurt(int cutHurt) {
	// this.cutHurt = cutHurt;
	// }
	//
	// public void setCutCritHurt(int cutCritHurt) {
	// this.cutCritHurt = cutCritHurt;
	// }
	//
	// public void setResist(int resist) {
	// this.resist = resist;
	// }
	//
	// public void setAddCure(int addCure) {
	// this.addCure = addCure;
	// }
	//
	// public void setCutCure(int cutCure) {
	// this.cutCure = cutCure;
	// }
	//
	// public void setLifeGrowUp(int lifeGrowUp) {
	// this.lifeGrowUp = lifeGrowUp;
	// }
	//
	// public void setAttackGrowUp(int attackGrowUp) {
	// this.attackGrowUp = attackGrowUp;
	// }
	//
	// public void setPhysicqueDefGrowUp(int physicqueDefGrowUp) {
	// this.physicqueDefGrowUp = physicqueDefGrowUp;
	// }
	//
	// public void setSpiritDefGrowUp(int spiritDefGrowUp) {
	// this.spiritDefGrowUp = spiritDefGrowUp;
	// }
	//
	// public void setEnchantExp(int enchantExp) {
	// this.enchantExp = enchantExp;
	// }
	//
	// public void setSkillLevel(int skillLevel) {
	// this.skillLevel = skillLevel;
	// }
	//
	// public void setAttackType(int attackType) {
	// this.attackType = attackType;
	// }
	//
	// public void setDodge(int dodge) {
	// this.dodge = dodge;
	// }
	//
	// public void setHit(int hit) {
	// this.hit = hit;
	// }
	//
	// public void setEnergyPerSecond(int energyPerSecond) {
	// this.energyPerSecond = energyPerSecond;
	// }
	//
	// public void setHardStraight(float hardStraight) {
	// this.hardStraight = hardStraight;
	// }
	//
	// public void setReactionTime(float reactionTime) {
	// this.reactionTime = reactionTime;
	// }
	//
	// public void setAttackDistance(float attackDistance) {
	// this.attackDistance = attackDistance;
	// }
	//
	// public void setAttackSpeed(float attackSpeed) {
	// this.attackSpeed = attackSpeed;
	// }
	//
	// public void setMoveSpeed(float moveSpeed) {
	// this.moveSpeed = moveSpeed;
	// }
	//
	// public void setAttackHurt(float attackHurt) {
	// this.attackHurt = attackHurt;
	// }
	//
	// public void setViewRange(float viewRange) {
	// this.viewRange = viewRange;
	// }
	//
	// public void setVolumeRadius(float volumeRadius) {
	// this.volumeRadius = volumeRadius;
	// }
	//
	// public void setDoHurt(float doHurt) {
	// this.doHurt = doHurt;
	// }

	public static class Builder {
		private int life; // 最大生命值...
		private int energy; // 能量值...
		private int physiqueAttack; // 物理攻击...
		private int spiritAttack;// 魔法攻击...
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
		private int pAttackGrowUp; // 物理攻击成长...
		private int sAttackGrowUp; // 法术攻击成长...
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

		public void setLife(int life) {
			this.life = life;
		}

		public void setEnergy(int energy) {
			this.energy = energy;
		}

		public void setPhysiqueAttack(int physiqueAttack) {
			this.physiqueAttack = physiqueAttack;
		}

		public void setSpiritAttack(int spiritAttack) {
			this.spiritAttack = spiritAttack;
		}

		public void setPhysiqueDef(int physiqueDef) {
			this.physiqueDef = physiqueDef;
		}

		public void setSpiritDef(int spiritDef) {
			this.spiritDef = spiritDef;
		}

		public void setAttackVampire(int attackVampire) {
			this.attackVampire = attackVampire;
		}

		public void setCritical(int critical) {
			this.critical = critical;
		}

		public void setCriticalHurt(int criticalHurt) {
			this.criticalHurt = criticalHurt;
		}

		public void setToughness(int toughness) {
			this.toughness = toughness;
		}

		public void setLifeReceive(int lifeReceive) {
			this.lifeReceive = lifeReceive;
		}

		public void setEnergyReceive(int energyReceive) {
			this.energyReceive = energyReceive;
		}

		public void setStruckEnergy(int struckEnergy) {
			this.struckEnergy = struckEnergy;
		}

		public void setAttackEnergy(int attackEnergy) {
			this.attackEnergy = attackEnergy;
		}

		public void setEnergyTrans(int energyTrans) {
			this.energyTrans = energyTrans;
		}

		public void setCutHurt(int cutHurt) {
			this.cutHurt = cutHurt;
		}

		public void setCutCritHurt(int cutCritHurt) {
			this.cutCritHurt = cutCritHurt;
		}

		public void setResist(int resist) {
			this.resist = resist;
		}

		public void setAddCure(int addCure) {
			this.addCure = addCure;
		}

		public void setCutCure(int cutCure) {
			this.cutCure = cutCure;
		}

		public void setLifeGrowUp(int lifeGrowUp) {
			this.lifeGrowUp = lifeGrowUp;
		}

		public void setpAttackGrowUp(int pAttackGrowUp) {
			this.pAttackGrowUp = pAttackGrowUp;
		}

		public void setsAttackGrowUp(int sAttackGrowUp) {
			this.sAttackGrowUp = sAttackGrowUp;
		}

		public void setPhysicqueDefGrowUp(int physicqueDefGrowUp) {
			this.physicqueDefGrowUp = physicqueDefGrowUp;
		}

		public void setSpiritDefGrowUp(int spiritDefGrowUp) {
			this.spiritDefGrowUp = spiritDefGrowUp;
		}

		public void setEnchantExp(int enchantExp) {
			this.enchantExp = enchantExp;
		}

		public void setSkillLevel(int skillLevel) {
			this.skillLevel = skillLevel;
		}

		public void setAttackType(int attackType) {
			this.attackType = attackType;
		}

		public void setDodge(int dodge) {
			this.dodge = dodge;
		}

		public void setHit(int hit) {
			this.hit = hit;
		}

		public void setEnergyPerSecond(int energyPerSecond) {
			this.energyPerSecond = energyPerSecond;
		}

		public void setHardStraight(float hardStraight) {
			this.hardStraight = hardStraight;
		}

		public void setReactionTime(float reactionTime) {
			this.reactionTime = reactionTime;
		}

		public void setAttackDistance(float attackDistance) {
			this.attackDistance = attackDistance;
		}

		public void setAttackSpeed(float attackSpeed) {
			this.attackSpeed = attackSpeed;
		}

		public void setMoveSpeed(float moveSpeed) {
			this.moveSpeed = moveSpeed;
		}

		public void setAttackHurt(float attackHurt) {
			this.attackHurt = attackHurt;
		}

		public void setViewRange(float viewRange) {
			this.viewRange = viewRange;
		}

		public void setVolumeRadius(float volumeRadius) {
			this.volumeRadius = volumeRadius;
		}

		public void setDoHurt(float doHurt) {
			this.doHurt = doHurt;
		}

		public AttrData build() {
			return new AttrData(life, energy, physiqueAttack, spiritAttack, physiqueDef, spiritDef, attackVampire, critical, criticalHurt, toughness, lifeReceive, energyReceive, struckEnergy,
					attackEnergy, energyTrans, cutHurt, cutCritHurt, resist, addCure, cutCure, lifeGrowUp, pAttackGrowUp, sAttackGrowUp, physicqueDefGrowUp, spiritDefGrowUp, enchantExp, skillLevel,
					attackType, dodge, hit, energyPerSecond, hardStraight, reactionTime, attackDistance, attackSpeed, moveSpeed, attackHurt, viewRange, volumeRadius, doHurt);
		}
	}
}