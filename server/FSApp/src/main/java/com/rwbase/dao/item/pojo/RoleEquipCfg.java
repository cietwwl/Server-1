package com.rwbase.dao.item.pojo;

public class RoleEquipCfg extends ItemBaseCfg{
	private int equipType;                //武器类型	
	private int careerType;				//职业
	private int openLevel;            //可穿戴等级
	private int enchantLimit;        //强化等级
	private int life;               //最大生命值提升
	private int attack;              //攻击提升
	private int physicqueDef;      //体魄防御提升
	private int spiritDef;        //精神防御提升
	private int attackVampire;     //攻击吸血提升
	private int critical;         //暴击率提升
	private int criticalHurt;                //暴击伤害提升
	private int toughness;            //韧性提升
	private int lifeReceive;          //生命回复提升
	private int energyReceive;       //能量回复提升
	private int energyTrans;      //能量转化提升
	private int attackSpeed;         //攻击速度提升
	private String hideAttr;		//隐藏属性
	private int attackHurt;			//伤害增加	
	private int cutHurt;			//伤害减免
	private int cutCritHurt;		//暴击伤害减免
	private int resist;				//抵抗
	private int addCure;			//收到治疗效果增加
	private int cutCure;			//收到治疗效果降低
	private String inlayLimit;
	
	public int getCareerType() {
		return careerType;
	}
	public void setCareerType(int careerType) {
		this.careerType = careerType;
	}
	public int getEquipType() {
		return equipType;
	}
	public void setEquipType(int equipType) {
		this.equipType = equipType;
	}
	public int getOpenLevel() {
		return openLevel;
	}
	public void setOpenLevel(int level) {
		this.openLevel = level;
	}
	public int getEnchantLimit() {
		return enchantLimit;
	}
	public void setEnchantLimit(int enchantLimit) {
		this.enchantLimit = enchantLimit;
	}
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
	public int getPhysicqueDef() {
		return physicqueDef;
	}
	public void setPhysicqueDef(int physicsDefense) {
		this.physicqueDef = physicsDefense;
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
		return energyReceive;
	}
	public void setEnergyReceive(int energyRecover) {
		this.energyReceive = energyRecover;
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
	public String getHideAttr() {
		return hideAttr;
	}
	public void setHideAttr(String hideAttr) {
		this.hideAttr = hideAttr;
	}
	public int getAttackHurt() {
		return attackHurt;
	}
	public void setAttackHurt(int attackHurt) {
		this.attackHurt = attackHurt;
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
	public String getInlayLimit() {
		return inlayLimit;
	}
	public void setInlayLimit(String inlayLimit) {
		this.inlayLimit = inlayLimit;
	}
	
}
