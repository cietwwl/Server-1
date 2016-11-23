package com.rwbase.dao.item.pojo;

public class ConsumeCfg extends ItemBaseCfg {
	private int isUse; // 是否可使用
	private int magicForgeExp; // 法宝锻造提供经验
	private int value; // 数值
	private String itemspit; // 物品吐槽
	private int moneyType;
	private int useLevel;// 使用等级

	public int getMoneyType() {
		return moneyType;
	}

	public String getItemspit() {
		return itemspit;
	}

	public void setItemspit(String itemspit) {
		this.itemspit = itemspit;
	}

	public int getIsUse() {
		return isUse;
	}

	public void setIsUse(int isUse) {
		this.isUse = isUse;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getMagicForgeExp() {
		return magicForgeExp;
	}

	public void setMagicForgeExp(int magicForgeExp) {
		this.magicForgeExp = magicForgeExp;
	}

	/**
	 * 获取使用等级
	 * 
	 * @return
	 */
	public int getUseLevel() {
		return useLevel;
	}
}