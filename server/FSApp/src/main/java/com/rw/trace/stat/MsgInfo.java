package com.rw.trace.stat;

public class MsgInfo {

	private final int max;
	private final int times;
	private final int totalAvgCost;
	private final int recentlyAvgCost;

	public MsgInfo(int max, int times, int totalAvgCost, int recentlyAvgCost) {
		this.max = max;
		this.times = times;
		this.totalAvgCost = totalAvgCost;
		this.recentlyAvgCost = recentlyAvgCost;
	}

	public int getMax() {
		return max;
	}

	public int getTimes() {
		return times;
	}

	public int getTotalAvgCost() {
		return totalAvgCost;
	}

	public int getRecentlyAvgCost() {
		return recentlyAvgCost;
	}

	@Override
	public String toString() {
		return "(max=" + max + ", times=" + times + ", totalAvgCost=" + totalAvgCost + ", recentlyAvgCost=" + recentlyAvgCost + ")";
	}

}
