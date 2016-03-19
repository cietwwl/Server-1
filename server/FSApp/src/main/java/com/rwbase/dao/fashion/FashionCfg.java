package com.rwbase.dao.fashion;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FashionCfg {

	private String id;
	private int type;
	private String name;
	private String icon;
	private String atlas;
	private int buyCost;
	private int renewCost;
	private int validity;
	private String model;
	private String hangPoint;
	private int sex;
	private int career;
	private String actionName;
	private String voice;
	private int suitId;
	
	private int life;
	private int attack;
	private int  physiqueDef;
	private int spiritDef;
	private int attackVampire;
	private int critical;
	private int criticalHurt;
	private int  toughness;
	private int  lifeReceive;
	private int energyReceive;
	
	private int lifePercent;
	private int  attackPercent;
	private int  physiqueDefPercent;
	private int spiritDefPercent;
	private int attackVampirePercent;
	private int criticalPercent;
	private int criticalHurtPercent;
	private int toughnessPercent;
	private int lifeReceivePercent;
	private int energyReceivePercent;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public int getBuyCost() {
		return buyCost;
	}
	public void setBuyCost(int buyCost) {
		this.buyCost = buyCost;
	}
	public int getRenewCost() {
		return renewCost;
	}
	public void setRenewCost(int renewCost) {
		this.renewCost = renewCost;
	}
	public int getValidity() {
		return validity;
	}
	public void setValidity(int validity) {
		this.validity = validity;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getHangPoint() {
		return hangPoint;
	}
	public void setHangPoint(String hangPoint) {
		this.hangPoint = hangPoint;
	}

	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getCareer() {
		return career;
	}
	public void setCareer(int career) {
		this.career = career;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getVoice() {
		return voice;
	}
	public void setVoice(String voice) {
		this.voice = voice;
	}
	public int getSuitId() {
		return suitId;
	}
	public void setSuitId(int suitId) {
		this.suitId = suitId;
	}
	public int getLife() {
		return life;
	}
	public void setLife(int life) {
		this.life = life;
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
	public int getLifePercent() {
		return lifePercent;
	}
	public void setLifePercent(int lifePercent) {
		this.lifePercent = lifePercent;
	}
	public int getAttackPercent() {
		return attackPercent;
	}
	public void setAttackPercent(int attackPercent) {
		this.attackPercent = attackPercent;
	}
	
	public int getSpiritDefPercent() {
		return spiritDefPercent;
	}
	public void setSpiritDefPercent(int spiritDefPercent) {
		this.spiritDefPercent = spiritDefPercent;
	}
	public int getAttackVampirePercent() {
		return attackVampirePercent;
	}
	public void setAttackVampirePercent(int attackVampirePercent) {
		this.attackVampirePercent = attackVampirePercent;
	}
	public int getCriticalPercent() {
		return criticalPercent;
	}
	public void setCriticalPercent(int criticalPercent) {
		this.criticalPercent = criticalPercent;
	}
	public int getCriticalHurtPercent() {
		return criticalHurtPercent;
	}
	public void setCriticalHurtPercent(int criticalHurtPercent) {
		this.criticalHurtPercent = criticalHurtPercent;
	}
	public int getToughnessPercent() {
		return toughnessPercent;
	}
	public void setToughnessPercent(int toughnessPercent) {
		this.toughnessPercent = toughnessPercent;
	}
	public int getLifeReceivePercent() {
		return lifeReceivePercent;
	}
	public void setLifeReceivePercent(int lifeReceivePercent) {
		this.lifeReceivePercent = lifeReceivePercent;
	}
	public int getEnergyReceivePercent() {
		return energyReceivePercent;
	}
	public void setEnergyReceivePercent(int energyReceivePercent) {
		this.energyReceivePercent = energyReceivePercent;
	}
	public int getPhysiqueDef() {
		return physiqueDef;
	}
	public void setPhysiqueDef(int physiqueDef) {
		this.physiqueDef = physiqueDef;
	}
	public int getPhysiqueDefPercent() {
		return physiqueDefPercent;
	}
	public void setPhysiqueDefPercent(int physiqueDefPercent) {
		this.physiqueDefPercent = physiqueDefPercent;
	}
	
}
