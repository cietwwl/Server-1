package com.rwbase.dao.item.pojo;

public class HeroEquipCfg extends ItemBaseCfg{
	private int equipType;                //武器类型	
	private int level;               //可穿戴等级
	private int enchantLimit;        //附灵等级上限
	private int life;               //最大生命值提升
	private int attack;              //攻击提升
	private int physiqueDef;      //体魄防御提升
	private int spiritDef;        //精神防御提升
	private int attackVampire;               //攻击吸血提升
	private int critical;         //暴击率提升
	private int criticalHurt;                //暴击伤害提升
	private int toughness;            //韧性提升
	private int lifeReceive;          //生命回复提升
	private int energyReceive;       //能量回复提升
	private int energyTrans;      //能量转化提升
	private int attackSpeed;         //攻击速度提升
	
	public int getEquipType() {
		return equipType;
	}
	public void setEquipType(int equipType) {
		this.equipType = equipType;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
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
	public int getPhysiqueDef() {
		return physiqueDef;
	}
	public void setPhysiqueDef(int physiqueDef) {
		this.physiqueDef = physiqueDef;
	}
}
