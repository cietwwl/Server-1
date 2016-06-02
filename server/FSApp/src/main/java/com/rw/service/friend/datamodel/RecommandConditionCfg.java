package com.rw.service.friend.datamodel;

public class RecommandConditionCfg {

	private int seqId; // 序列
	private int days; // 天数限制
	private int desLevel; // 下降的等级
	private int incLevel; // 上升的大需
	private int count; // 推荐数量
	private int randomCount;	//随机个数
	
	public int getSeqId() {
		return seqId;
	}

	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getDesLevel() {
		return desLevel;
	}

	public void setDesLevel(int desLevel) {
		this.desLevel = desLevel;
	}

	public int getIncLevel() {
		return incLevel;
	}

	public void setIncLevel(int incLevel) {
		this.incLevel = incLevel;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getRandomCount() {
		return randomCount;
	}

	public void setRandomCount(int randomCount) {
		this.randomCount = randomCount;
	}

}
