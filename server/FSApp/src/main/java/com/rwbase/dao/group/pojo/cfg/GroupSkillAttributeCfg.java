package com.rwbase.dao.group.pojo.cfg;

import java.util.Map;

import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeConst;
import com.rwbase.common.attribute.AttributeType;
import com.rwbase.common.attribute.AttributeUtils;

/*
 * @author HC
 * @date 2016年3月12日 下午12:22:45
 * @Description 帮派技能属性加成
 */
public class GroupSkillAttributeCfg {
	private int id;
	private int energy;// 能量值
	private int life;// 生命
	private int attack;// 攻击
	private int physiqueDef;// 体魄防御
	private int spiritDef;// 精神防御
	private int hit;// 命中
	private int dodge;// 闪避
	private int critical;// 暴击率
	private int toughness;// 韧性
	private int resist;// 抵抗
	private int attackHurt;// 攻击伤害
	private int cutHurt;// 伤害减免
	private int criticalHurt;// 暴击伤害提升
	private int cutCritHurt;// 暴击伤害减免
	private int lifeReceive;// 生命回复
	private int energyReceive;// 能量值回复
	private int attackVampire;// 攻击吸血
	private int attackSpeed;// 攻击速度
	private int moveSpeed;// 移动速度
	private int addCure;// 受到治疗效果增加
	private int cutCure;// 受到治疗效果减少
	// ///////////////////////////////////////////百分比
	private int attackPercent;// 攻击百分比
	private int criticalHurtPercent;// 暴击伤害提升百分比
	private int criticalPercent;// 暴击伤害提升百分比
	private int attackVampirePercent; // 吸血百分比
	private int spiritDefPercent;// 法术防御百分比
	private int dodgePercent;// 闪避百分比
	private int physiqueDefPercent;// 物理防御百分比
	private int attackHurtPercent; // 伤害减免百分比
	private int lifePercent;// 生命百分比

	private String attrData;// 增加的固定值属性
	private String precentAttrData;// 增加的百分比属性
	private Map<Integer, Integer> attrDataMap;// 增加固定值属性
	private Map<Integer, Integer> precentAttrDataMap;// 增加的百分比属性

	public int getId() {
		return id;
	}

	public int getEnergy() {
		return energy;
	}

	public int getLife() {
		return life;
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

	public int getHit() {
		return hit;
	}

	public int getDodge() {
		return dodge;
	}

	public int getCritical() {
		return critical;
	}

	public int getToughness() {
		return toughness;
	}

	public int getResist() {
		return resist;
	}

	public int getAttackHurt() {
		return attackHurt;
	}

	public int getCutHurt() {
		return cutHurt;
	}

	public int getCriticalHurt() {
		return criticalHurt;
	}

	public int getCutCritHurt() {
		return cutCritHurt;
	}

	public int getLifeReceive() {
		return lifeReceive;
	}

	public int getEnergyReceive() {
		return energyReceive;
	}

	public int getAttackVampire() {
		return attackVampire;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public int getAddCure() {
		return addCure;
	}

	public int getCutCure() {
		return cutCure;
	}

	public int getAttackPercent() {
		return attackPercent;
	}

	public int getCriticalHurtPercent() {
		return criticalHurtPercent;
	}

	public int getCriticalPercent() {
		return criticalPercent;
	}

	public int getAttackVampirePercent() {
		return attackVampirePercent;
	}

	public int getSpiritDefPercent() {
		return spiritDefPercent;
	}

	public int getDodgePercent() {
		return dodgePercent;
	}

	public int getPhysiqueDefPercent() {
		return physiqueDefPercent;
	}

	public int getAttackHurtPercent() {
		return attackHurtPercent;
	}

	public int getLifePercent() {
		return lifePercent;
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