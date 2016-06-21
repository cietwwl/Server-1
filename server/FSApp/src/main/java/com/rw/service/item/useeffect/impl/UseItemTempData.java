package com.rw.service.item.useeffect.impl;

public class UseItemTempData {
	
	private String id;// 道具Id
	private int modelId;// 物品Id
	private int count;// 物品数量
	private int value;//数值
	public UseItemTempData(String id, int modelId, int count, int value) {
		super();
		this.id = id;
		this.modelId = modelId;
		this.count = count;
		this.value = value;
	}
	public String getId() {
		return id;
	}
	public int getModelId() {
		return modelId;
	}
	public int getCount() {
		return count;
	}
	public int getValue() {
		return value;
	}
	
	public void setCount(int num){
		count = num;
	}

}
