package com.playerdata.battleVerify.damageControll;

public class DamageControllCfg {

	/**
	 * 等级
	 */
	private int lv;
	
	/**
	 * 标准最高攻击
	 */
	private long commonAttack;
	
	//职业最高攻击
	private long careerAttack;
	//职业最高暴击
	private long critical;
	
	//职业最高暴伤
	private long criticalHurt;
	
	//普攻最高DPS
	private long commonDPS;
	//必杀最高DPS
	private long firstSkillDPS;
	//2技能最高DPS
	private long secSkillDPS;
	
	//3技能最高DPS
	private long thirdSkillDPS;
	
	//4技能最高DPS
	private long forthSkillDPS;
	
	//每3秒最高伤害(有防御)
	private long _3SDefDamage;
	
	//每3秒最高伤害（无防御）
	private long _3SNoDefDamage;
	
	//一场战斗单人最高伤害
	private long singleRoleMaxHurt;
	
	//一场战斗全队最高伤害
	private long teamMaxHurt;
	
	//单次攻击最高伤害
	private long singleHitMaxHurt;

	public int getLv() {
		return lv;
	}

	
	public long getCommonAttack() {
		return commonAttack;
	}

	public long getCareerAttack() {
		return careerAttack;
	}

	public long getCritical() {
		return critical;
	}

	public long getCriticalHurt() {
		return criticalHurt;
	}

	public long getCommonDPS() {
		return commonDPS;
	}

	public long getFirstSkillDPS() {
		return firstSkillDPS;
	}

	public long getSecSkillDPS() {
		return secSkillDPS;
	}

	public long getThirdSkillDPS() {
		return thirdSkillDPS;
	}

	public long getForthSkillDPS() {
		return forthSkillDPS;
	}

	public long get_3SDefDamage() {
		return _3SDefDamage;
	}

	public long get_3SNoDefDamage() {
		return _3SNoDefDamage;
	}

	public long getSingleRoleMaxHurt() {
		return singleRoleMaxHurt;
	}

	public long getTeamMaxHurt() {
		return teamMaxHurt;
	}

	public long getSingleHitMaxHurt() {
		return singleHitMaxHurt;
	}
	
	
	
	
}
