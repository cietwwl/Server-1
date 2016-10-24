package com.rwbase.dao.spriteattach.pojo;

import java.util.HashMap;

public class SpriteAttachRoleCfg {
	private String roleId;
	private int spriteItem1;
	private int spriteItem2;
	private int spriteItem3;
	private int spriteItem4;
	private int spriteItem5;
	private int spriteItem6;

	private HashMap<Integer, Integer> IndexMap = new HashMap<Integer, Integer>();

	public String getRoleId() {
		return roleId;
	}

	public int getSpriteItem1() {
		return spriteItem1;
	}

	public int getSpriteItem2() {
		return spriteItem2;
	}

	public int getSpriteItem3() {
		return spriteItem3;
	}

	public int getSpriteItem4() {
		return spriteItem4;
	}

	public int getSpriteItem5() {
		return spriteItem5;
	}

	public int getSpriteItem6() {
		return spriteItem6;
	}

	public void addIndexMap(int spriteItemId, int index) {
		IndexMap.put(spriteItemId, index);
	}

	public int getIndex(int spriteItemId) {
		return IndexMap.get(spriteItemId);
	}
}
