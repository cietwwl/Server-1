package com.rwbase.dao.skill.pojo;


public class SkillCfg {

	private String skillId;
	private String nextSillId;//下一个技能
	private String name;//技能名...
	private String desc;//描述...
	private String attrDesc;//属性描述
	
	private int level;//等级...
	private int life; //生命值...
	private int energy; // 能量值...
	private int attack; // 体魄攻击..
	private int physiqueDef; // 体魄防御...
	private int spiritDef; // 精神防御...
	private int attackVampire; // 攻击吸血...
	private int critical; // 暴击率...
	private int criticalHurt; // 暴击伤害提升...
	private int toughness; // 韧性...
	private int lifeReceive; // 生命回复...
	private int energyReceive; // 能量回复...
	private int energyTrans; // 能量转化...
	private int hit;//命中
	private int dodge;//闪避 
	private int property;//技能属性...
	private int hitRate;//是否必然命中...
	private int skillDamage; //技能伤害
	 
	private float moveSpeed; // 移动速度...
	private float attackSpeed; // 攻击速度...
	
	private int roleQuality;//佣兵要判断此条件
	private int roleLevel;//主角仅此条件
	private String skillEffectId;
	private String skillEffectId2;
    public String icon;   //图标名...
    public String atlas;  //图集名...
	private int extraDamage;//额外伤害。。。
    private float skillRate;//技能系数
	private String controlId;
	private String buffId;
	private String floatTip;
	private String selfBuffId;
	
	public SkillCfg() {
	}

	public int getHitRate()
	{
		return hitRate;
	}

	public void setHitRate(int hitRate)
	{
		this.hitRate = hitRate;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}
	
	public String getSkillId() {
		return skillId;
	}


	public void setSkillId(String skillId) {
		this.skillId = skillId;
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
	
	

	public void setToughness(int toughness) {
		this.toughness = toughness;
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


	public void setCriticalHurt(int criHamPromot) {
		this.criticalHurt = criHamPromot;
	}


	public int getToughness() {
		return toughness;
	}


	public void setToughnessVal(int toughnessVal) {
		this.toughness = toughnessVal;
	}


	public int getLifeReceive() {
		return lifeReceive;
	}


	public void setLifeReceive(int lifeReceiveVal) {
		this.lifeReceive = lifeReceiveVal;
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

	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getNextSillId() {
		return nextSillId;
	}


	public void setNextSillId(String nextSillId) {
		this.nextSillId = nextSillId;
	}


	public int getRoleQuality() {
		return roleQuality;
	}


	public void setRoleQuality(int roleQuality) {
		this.roleQuality = roleQuality;
	}


	public int getRoleLevel() {
		return roleLevel;
	}


	public void setRoleLevel(int roleLevel) {
		this.roleLevel = roleLevel;
	}

	public String getSkillEffectId() {
		return skillEffectId;
	}

	public void setSkillEffectId(String skillEffectId) {
		this.skillEffectId = skillEffectId;
	}
	
	public String getSkillEffectId2() {
		return skillEffectId2;
	}

	public void setSkillEffectId2(String skillEffectId2) {
		this.skillEffectId2 = skillEffectId2;
	}

	public int getProperty() {
		return property;
	}

	public void setProperty(int property) {
		this.property = property;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAtlas() {
		return atlas;
	}

	public void setAtlas(String atlas) {
		this.atlas = atlas;
	}

	public String getAttrDesc() {
		return attrDesc;
	}

	public void setAttrDesc(String attrDesc) {
		this.attrDesc = attrDesc;
	}

	public int getExtraDamage() {
		return extraDamage;
	}

	public void setExtraDamage(int extraDamage) {
		this.extraDamage = extraDamage;
	}

	public float getSkillRate() {
		return skillRate;
	}

	public void setSkillRate(float skillRate) {
		this.skillRate = skillRate;
	}

	public String getControlId() {
		return controlId;
	}

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public String getBuffId() {
		return buffId;
	}

	public void setBuffId(String buffId) {
		this.buffId = buffId;
	}

	public String getFloatTip() {
		return floatTip;
	}

	public void setFloatTip(String floatTip) {
		this.floatTip = floatTip;
	}

	public float getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public float getDodge() {
		return dodge;
	}

	public void setDodge(int dodge) {
		this.dodge = dodge;
	}

	public int getSkillDamage() {
		return skillDamage;
	}

	public void setSkillDamage(int skillDamage) {
		this.skillDamage = skillDamage;
	}

	public String getSelfBuffId() {
		return selfBuffId;
	}

	public void setSelfBuffId(String selfBuffId) {
		this.selfBuffId = selfBuffId;
	}

}
