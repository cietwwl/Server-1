package com.rw.service.dropitem;

public class DropGuaranteeRecord {

	// 记录道具ID
	private int id;
	// 记录次数，正数表示连续掉落的次数，负数表示连续不掉的次数
	private int t;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

}
