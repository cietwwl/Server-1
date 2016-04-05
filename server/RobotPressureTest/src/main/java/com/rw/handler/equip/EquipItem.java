package com.rw.handler.equip;

import com.rw.dataSyn.SynItem;

/*
 * @author HC
 * @date 2016年4月1日 下午2:40:32
 * @Description 
 */
public class EquipItem implements SynItem {

	private String id; // ownerId + slotId 装备唯一id...
	private String ownerId;// 装备者（佣兵或主角）的id...
	private int equipIndex; // 装备位置Id...
	private int modelId;// 物品Id...
	private int level; // 等级...
	private int exp; // 经验...

	@Override
	public String getId() {
		return id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public int getEquipIndex() {
		return equipIndex;
	}

	public int getModelId() {
		return modelId;
	}

	public int getLevel() {
		return level;
	}

	public int getExp() {
		return exp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public void setEquipIndex(int equipIndex) {
		this.equipIndex = equipIndex;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}
}