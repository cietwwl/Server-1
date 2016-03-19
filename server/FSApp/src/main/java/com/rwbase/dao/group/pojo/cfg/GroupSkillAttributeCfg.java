package com.rwbase.dao.group.pojo.cfg;

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
}