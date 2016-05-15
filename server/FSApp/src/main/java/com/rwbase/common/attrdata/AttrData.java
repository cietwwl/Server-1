package com.rwbase.common.attrdata;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.BeanCopyer;
import com.common.BeanOperationHelper;
import com.common.IBeanNameFixAction;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class AttrData implements AttrDataIF {

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

	public AttrData() {
	}

	public AttrData(int life, int energy, int attack, int physiqueDef, int spiritDef, int attackVampire, int critical, int criticalHurt, int toughness, int lifeReceive, int energyReceive,
			int struckEnergy, int attackEnergy, int energyTrans, int cutHurt, int cutCritHurt, int resist, int addCure, int cutCure, int lifeGrowUp, int attackGrowUp, int physicqueDefGrowUp,
			int spiritDefGrowUp, int enchantExp, int skillLevel, int attackType, int dodge, int hit, int energyPerSecond, float hardStraight, float reactionTime, float attackDistance,
			float attackSpeed, float moveSpeed, float attackHurt, float viewRange, float volumeRadius, float doHurt) {
		this.life = life;
		this.energy = energy;
		this.attack = attack;
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
		this.attackGrowUp = attackGrowUp;
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
		BeanOperationHelper.addPercentObject(this, target, AttrDataHelper.DIVISION);
		return this;
	}

	public AttrData addPercent(int mutiNumber) {
		BeanOperationHelper.addPercent(this, mutiNumber, AttrDataHelper.DIVISION);
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

	public static AttrData fromObject(Object source) {
		AttrData data = new AttrData();
		BeanCopyer.copy(source, data);
		return data;
	}

	public static AttrData fromPercentObject(Object source) {
		AttrData data = new AttrData();
		BeanCopyer.copy(source, data, new IBeanNameFixAction() {
			@Override
			public String doFix(String name) {
				return StringUtils.substringBefore(name, "Percent");
			}
		});
		return data;
	}

	public static AttrData fromPercentObjectToAttrData(Object source) {
		AttrData data = new AttrData();
		BeanCopyer.copyFormPercentObject(source, data, new IBeanNameFixAction() {
			@Override
			public String doFix(String name) {
				return StringUtils.substringBefore(name, "Percent");
			}
		});
		return data;
	}

	/**
	 * 根据配置表字符串加入值
	 * 
	 * @param cfgStr example: life:30,attack:40
	 * @return
	 */
	public static AttrData fromCfgStr(String cfgStr) {
		AttrData data = null;
		final String Attr_Split = ",";
		final String File_Value_Split = ":";
		String[] attrCfg = cfgStr.split(Attr_Split);
		Map<String, String> attrMap = new HashMap<String, String>();
		for (String attrValueTmp : attrCfg) {
			String[] split = attrValueTmp.trim().split(File_Value_Split);
			if (split.length == 2) {
				String filedName = split[0];
				String filedValue = split[1];
				attrMap.put(filedName, filedValue);
			}
		}
		if (attrMap.size() > 0) {
			data = fromMap(attrMap);
		}
		return data;
	}

	private static AttrData fromMap(Map<String, String> mapData) {
		AttrData data = new AttrData();
		BeanOperationHelper.plus(data, mapData);
		return data;
	}

	public int getLife() {
		return life;
	}

	public int getEnergy() {
		return energy;
	}

	public int getAttack() {
		return attack;
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

	public int getAttackGrowUp() {
		return attackGrowUp;
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

	// TODO HC ////////////////////////////////////////////以后会删除掉

	public void setLife(int life) {
		this.life = life;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void setAttack(int attack) {
		this.attack = attack;
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

	public void setAttackGrowUp(int attackGrowUp) {
		this.attackGrowUp = attackGrowUp;
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

	public static class Builder {
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

		public void setLife(int life) {
			this.life = life;
		}

		public void setEnergy(int energy) {
			this.energy = energy;
		}

		public void setAttack(int attack) {
			this.attack = attack;
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

		public void setAttackGrowUp(int attackGrowUp) {
			this.attackGrowUp = attackGrowUp;
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
			return new AttrData(life, energy, attack, physiqueDef, spiritDef, attackVampire, critical, criticalHurt, toughness, lifeReceive, energyReceive, struckEnergy, attackEnergy, energyTrans,
					cutHurt, cutCritHurt, resist, addCure, cutCure, lifeGrowUp, attackGrowUp, physicqueDefGrowUp, spiritDefGrowUp, enchantExp, skillLevel, attackType, dodge, hit, energyPerSecond,
					hardStraight, reactionTime, attackDistance, attackSpeed, moveSpeed, attackHurt, viewRange, volumeRadius, doHurt);
		}
	}
}