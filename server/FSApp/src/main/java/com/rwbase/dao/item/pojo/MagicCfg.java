package com.rwbase.dao.item.pojo;

public class MagicCfg extends ItemBaseCfg {
	private int property; // 属性
	private int smeltperc; // 熔炼生成概率
	private String trainItemId; // 锻造材料ID
	private int skillId; // 法宝技能ID
	private String itemspit; // 物品吐槽
	private int energyReceive; // 能量回复
	private int attackType;
	private int struckEnergy;
	private int magicType;
	private int composeItemID;
	private int composeNeedNum;
	private int composeCostCoin;
	private String inlayLimit;
	private String hideAttr;
	private int initialEnergy;// 初始能量值

	public String getItemspit() {
		return itemspit;
	}

	public void setItemspit(String itemspit) {
		this.itemspit = itemspit;
	}

	public int getProperty() {
		return property;
	}

	public void setProperty(int property) {
		this.property = property;
	}

	public int getSmeltperc() {
		return smeltperc;
	}

	public void setSmeltperc(int smeltperc) {
		this.smeltperc = smeltperc;
	}

	public String getTrainItemId() {
		return trainItemId;
	}

	public void setTrainItemId(String trainItemId) {
		this.trainItemId = trainItemId;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public int getEnergyReceive() {
		return energyReceive;
	}

	public void setEnergyReceive(int energyReceive) {
		this.energyReceive = energyReceive;
	}

	public int getAttackType() {
		return attackType;
	}

	public void setAttackType(int attackType) {
		this.attackType = attackType;
	}

	public int getStruckEnergy() {
		return struckEnergy;
	}

	public void setStruckEnergy(int struckEnergy) {
		this.struckEnergy = struckEnergy;
	}

	public int getMagicType() {
		return magicType;
	}

	public void setMagicType(int magicType) {
		this.magicType = magicType;
	}

	public int getComposeItemID() {
		return composeItemID;
	}

	public void setComposeItemID(int composeItemID) {
		this.composeItemID = composeItemID;
	}

	public int getComposeNeedNum() {
		return composeNeedNum;
	}

	public void setComposeNeedNum(int composeNeedNum) {
		this.composeNeedNum = composeNeedNum;
	}

	public int getComposeCostCoin() {
		return composeCostCoin;
	}

	public void setComposeCostCoin(int composeCostCoin) {
		this.composeCostCoin = composeCostCoin;
	}

	public String getInlayLimit() {
		return inlayLimit;
	}

	public void setInlayLimit(String inlayLimit) {
		this.inlayLimit = inlayLimit;
	}

	public String getHideAttr() {
		return hideAttr;
	}

	public void setHideAttr(String hideAttr) {
		this.hideAttr = hideAttr;
	}

	public int getInitialEnergy() {
		return initialEnergy;
	}

	public void setInitialEnergy(int initialEnergy) {
		this.initialEnergy = initialEnergy;
	}
}