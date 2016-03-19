package com.rwbase.dao.item.pojo;

public class SoulStoneCfg extends ItemBaseCfg{
	private int composeTargetId;     //可合成佣兵Id
	private int composeCount;        //合成所需数量
	private int star;
	
	public int getComposeTargetId() {
		return composeTargetId;
	}
	public void setComposeTargetId(int composeTargetId) {
		this.composeTargetId = composeTargetId;
	}
	public int getComposeCount() {
		return composeCount;
	}
	public void setComposeCount(int composeCount) {
		this.composeCount = composeCount;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
}