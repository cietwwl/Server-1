package com.rw.service.guide.datamodel;

public class GiveItemCfg {
	private int key; // key
	private String modleId; // 物品ID（不能为空）
	private int count; // 赠送数量

	public int getKey() {
		return key;
	}

	public String getModleId() {
		return modleId;
	}

	public int getCount() {
		return count;
	}

	public void ExtraInitAfterLoad() {
	}
}
