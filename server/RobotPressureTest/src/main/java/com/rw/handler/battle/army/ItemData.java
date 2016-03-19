package com.rw.handler.battle.army;

import java.util.concurrent.ConcurrentHashMap;


public class ItemData{
	private String id;// 道具Id
	private int modelId;// 物品Id
	private int count;// 物品数量
	private String userId;// 角色Id
	private ConcurrentHashMap<Integer, String> allExtendAttr = new ConcurrentHashMap<Integer, String>();// 扩展属性,
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public ConcurrentHashMap<Integer, String> getAllExtendAttr() {
		return allExtendAttr;
	}
	public void setAllExtendAttr(ConcurrentHashMap<Integer, String> allExtendAttr) {
		this.allExtendAttr = allExtendAttr;
	}
	
	
}