package com.bm.rank.arena;

public class ArenaSettlement {

	private int career; // 职业
	private volatile long getRewardMillis;
	private volatile long settleMillis; // 这个可以只存一份

	public long getGetRewardMillis() {
		return getRewardMillis;
	}

	public void setGetRewardMillis(long getRewardMillis) {
		this.getRewardMillis = getRewardMillis;
	}

	public int getCareer() {
		return career;
	}

	public void setCareer(int career) {
		this.career = career;
	}

	public long getSettleMillis() {
		return settleMillis;
	}

	public void setSettleMillis(long settleMillis) {
		this.settleMillis = settleMillis;
	}

}
