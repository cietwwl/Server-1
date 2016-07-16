package com.playerdata.hero.core;

import org.codehaus.jackson.annotate.JsonProperty;

import com.rwbase.common.attrdata.AttrDataIF;

public class FSHeroAttrDelegator  implements AttrDataIF {
	
	private static final String _KEY_LIFE = "1";
	private static final String _KEY_ENERGY = "2";
	private static final String _KEY_PHYSIQUE_ATTACK = "3";
	private static final String _KEY_SPIRIT_ATTACK = "4";
	private static final String _KEY_PHYSIQUE_DEF = "5";
	private static final String _KEY_SPIRIT_DEF = "6";
	private static final String _KEY_ATTACK_VAMPIRE = "7";
	private static final String _KEY_CRITICAL = "8";
	private static final String _KEY_CRITICAL_HURT = "9";
	private static final String _KEY_TOUGHNESS = "10";
	private static final String _KEY_LIFE_RECEIVE = "11";
	private static final String _KEY_ENERGY_RECEIVE = "12";
	private static final String _KEY_STRUCK_ENERGY = "13";
	private static final String _KEY_ATTACK_ENERGY = "14";
	private static final String _KEY_ENERGY_TRANS = "15";
	private static final String _KEY_CUT_HURT = "16";
	private static final String _KEY_CUT_CRIT_HURT = "17";
	private static final String _KEY_RESIST = "18";
	private static final String _KEY_ADD_CURE = "19";
	private static final String _KEY_CUT_CURE = "20";
	private static final String _KEY_LIFE_GROW_UP = "21";
	private static final String _KEY_P_ATTACK_GROW_UP = "22";
	private static final String _KEY_S_ATTACK_GROW_UP = "23";
	private static final String _KEY_PHYSICQUE_DEF_GROW_UP = "24";
	private static final String _KEY_SPIRIT_DEF_GROW_UP = "25";
	private static final String _KEY_ENCHANT_EXP = "26";
	private static final String _KEY_SKILL_LEVEL = "27";
	private static final String _KEY_ATTACK_TYPE = "28";
	private static final String _KEY_DODGE = "29";
	private static final String _KEY_HIT = "30";
	private static final String _KEY_ENERGY_PER_SECOND = "31";
	private static final String _KEY_HARD_STRAIGHT = "32";
	private static final String _KEY_REACTION_TIME = "33";
	private static final String _KEY_ATTACK_DISTANCE = "34";
	private static final String _KEY_ATTACK_SPEED = "35";
	private static final String _KEY_MOVE_SPEED = "36";
	private static final String _KEY_ATTACK_HURT = "37";
	private static final String _KEY_VIEW_RANGE = "38";
	private static final String _KEY_VOLUME_RADIUS = "39";
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
	
	public FSHeroAttrDelegator() {
	}
	
	public void update(AttrDataIF target) {
		this.life = target.getLife();
		this.energy = target.getEnergy();
		this.physiqueAttack = target.getPhysiqueAttack();
		this.spiritAttack = target.getSpiritAttack();
		this.spiritDef = target.getSpiritDef();
		this.attackVampire = target.getAttackVampire();
		this.critical = target.getCritical();
		this.criticalHurt = target.getCriticalHurt();
		this.toughness = target.getToughness();
		this.lifeReceive = target.getLifeReceive();
		this.energyReceive = target.getEnergyReceive();
		this.struckEnergy = target.getStruckEnergy();
		this.energyTrans = target.getEnergyTrans();
		this.cutHurt = target.getCutHurt();
		this.cutCritHurt = target.getCutCritHurt();
		this.resist = target.getResist();
		this.addCure = target.getAddCure();
		this.cutCure = target.getCutCure();
		this.lifeGrowUp = target.getLifeGrowUp();
		this.pAttackGrowUp = target.getpAttackGrowUp();
		this.sAttackGrowUp = target.getSAttackGrowUp();
		this.physicqueDefGrowUp = target.getPhysicqueDefGrowUp();
		this.spiritDefGrowUp = target.getSpiritDefGrowUp();
		this.enchantExp = target.getEnchantExp();
		this.skillLevel = target.getSkillLevel();
		this.attackType = target.getAttackType();
		this.dodge = target.getDodge();
		this.hit = target.getHit();
		this.energyPerSecond = target.getEnergyPerSecond();
		
		this.hardStraight = target.getHardStraight();
		this.reactionTime = target.getReactionTime();
		this.attackDistance = target.getAttackDistance();
		this.moveSpeed = target.getMoveSpeed();
		this.attackHurt = target.getAttackHurt();
		this.viewRange = target.getViewRange();
		this.volumeRadius = target.getVolumeRadius();
		this.doHurt = target.getDoHurt();
	}
	
	@Override
	public int getLife() {
		return life;
	}

	@Override
	public int getEnergy() {
		return energy;
	}

	@Override
	public int getPhysiqueAttack() {
		return physiqueAttack;
	}

	@Override
	public int getSpiritAttack() {
		return spiritAttack;
	}

	@Override
	public int getPhysiqueDef() {
		return physiqueDef;
	}

	@Override
	public int getSpiritDef() {
		return spiritDef;
	}

	@Override
	public int getAttackVampire() {
		return attackVampire;
	}

	@Override
	public int getCritical() {
		return critical;
	}

	@Override
	public int getCriticalHurt() {
		return criticalHurt;
	}

	@Override
	public int getToughness() {
		return toughness;
	}

	@Override
	public int getLifeReceive() {
		return lifeReceive;
	}

	@Override
	public int getEnergyReceive() {
		return energyReceive;
	}

	@Override
	public int getStruckEnergy() {
		return struckEnergy;
	}

	@Override
	public int getAttackEnergy() {
		return attackEnergy;
	}

	@Override
	public int getEnergyTrans() {
		return energyTrans;
	}

	@Override
	public int getCutHurt() {
		return cutHurt;
	}

	@Override
	public int getCutCritHurt() {
		return cutCritHurt;
	}

	@Override
	public int getResist() {
		return resist;
	}

	@Override
	public int getAddCure() {
		return addCure;
	}

	@Override
	public int getCutCure() {
		return cutCure;
	}

	@Override
	public int getLifeGrowUp() {
		return lifeGrowUp;
	}

	@Override
	public int getpAttackGrowUp() {
		return pAttackGrowUp;
	}

	@Override
	public int getSAttackGrowUp() {
		return sAttackGrowUp;
	}

	@Override
	public int getPhysicqueDefGrowUp() {
		return physicqueDefGrowUp;
	}

	@Override
	public int getSpiritDefGrowUp() {
		return spiritDefGrowUp;
	}

	@Override
	public int getEnchantExp() {
		return enchantExp;
	}

	@Override
	public int getSkillLevel() {
		return skillLevel;
	}

	@Override
	public int getAttackType() {
		return attackType;
	}

	@Override
	public int getDodge() {
		return dodge;
	}

	@Override
	public int getHit() {
		return hit;
	}

	@Override
	public int getEnergyPerSecond() {
		return energyPerSecond;
	}

	@Override
	public float getHardStraight() {
		return hardStraight;
	}

	@Override
	public float getReactionTime() {
		return reactionTime;
	}

	@Override
	public float getAttackDistance() {
		return attackDistance;
	}

	@Override
	public float getAttackSpeed() {
		return attackSpeed;
	}

	@Override
	public float getMoveSpeed() {
		return moveSpeed;
	}

	@Override
	public float getAttackHurt() {
		return attackHurt;
	}

	@Override
	public float getViewRange() {
		return viewRange;
	}

	@Override
	public float getVolumeRadius() {
		return volumeRadius;
	}

	@Override
	public float getDoHurt() {
		return doHurt;
	}
	
	
	public static void main(String[] args) {
		java.lang.reflect.Field[] fields = FSHeroAttrDelegator.class.getDeclaredFields();
		int count = 1;
		for(java.lang.reflect.Field f : fields) {
			if (f.getType() == int.class || f.getType() == float.class) {
				String fName = f.getName();
				char c;
				StringBuilder strBld = new StringBuilder();
				StringBuilder fieldBuilder = new StringBuilder();
				for(int i = 0; i < fName.length(); i++) {
					c = fName.charAt(i);
					if (Character.getType(c) == Character.UPPERCASE_LETTER) {
						fieldBuilder.append(strBld.toString().toUpperCase()).append("_");
						strBld = new StringBuilder();
					}
					strBld.append(c);
				}
				fieldBuilder.append(strBld.toString().toUpperCase());
				System.out.println("private static final String _KEY_" + fieldBuilder.toString() + " = \"" + count + "\";");
				count++;
			}
		}
	}
}
