package com.rw.service.gamble.datamodel;

import com.common.BaseConfig;

public class GambleDropCfg extends BaseConfig{
	private int key; // 关键字段
	private int itemGroup; // 物品组
	private String itemID; // 道具ID
	private int weight; // 权重
	private int slotCount; // 道具叠加数

	public int getKey() {
		return key;
	}

	public int getItemGroup() {
		return itemGroup;
	}

	public String getItemID() {
		return itemID;
	}

	public int getWeight() {
		return weight;
	}

	public int getSlotCount() {
		return slotCount;
	}

}