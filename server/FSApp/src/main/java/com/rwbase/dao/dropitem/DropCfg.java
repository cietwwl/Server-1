package com.rwbase.dao.dropitem;

public class DropCfg {

	private int id; // 主键
	private int itemCfgId; // 道具模板ID
	private int baseRate; // 万分比概率
	private int dropCount; // 数量
	private int itemsFormula; // 掉落规则ID

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemCfgId() {
		return itemCfgId;
	}

	public void setItemCfgId(int itemCfgId) {
		this.itemCfgId = itemCfgId;
	}

	public int getBaseRate() {
		return baseRate;
	}

	public void setBaseRate(int baseRate) {
		this.baseRate = baseRate;
	}

	public int getDropCount() {
		return dropCount;
	}

	public void setDropCount(int dropCount) {
		this.dropCount = dropCount;
	}

	public int getItemsFormula() {
		return itemsFormula;
	}

	public void setItemsFormula(int itemsFormula) {
		this.itemsFormula = itemsFormula;
	}

}