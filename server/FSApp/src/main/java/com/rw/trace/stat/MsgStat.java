package com.rw.trace.stat;

import com.rw.fsutil.common.PairKey;

public class MsgStat {

	private final PairKey<Object, Object> key;
	private final String name;
	private int total;
	private int times;
	private final int recentlyMax;
	private final int maxIndex;
	private int recentlyCount;
	private int[] recentlyData;
	private int index;
	private int max;

	public MsgStat(String name, PairKey<Object, Object> key, int recentlyMax) {
		this.key = key;
		this.name = name;
		this.recentlyMax = recentlyMax;
		this.maxIndex = recentlyMax - 1;
		this.recentlyData = new int[recentlyMax];
	}

	public synchronized void add(long cost_) {
		int cost = (int) cost_;
		long preCost = recentlyData[index];
		recentlyData[index] = cost;
		// update index
		if (index >= maxIndex) {
			index = 0;
		} else {
			index++;
		}
		total += cost;
		if (++times > recentlyMax) {
			recentlyCount -= preCost;
		}
		recentlyCount += cost;
		if (cost > max) {
			max = cost;
		}
	}

	public synchronized MsgInfo getMsgInfo() {
		int totalAvgCost = total / times;
		int recentlyAvgCost;
		if (times >= recentlyMax) {
			recentlyAvgCost = recentlyCount / recentlyMax;
		} else {
			recentlyAvgCost = recentlyCount / times;
		}
		return new MsgInfo(max, times, totalAvgCost, recentlyAvgCost);
	}

	public int getTotal() {
		return total;
	}

	public int getTimes() {
		return times;
	}

	@Override
	public String toString() {
		Object key2 = key.secondKey;
		StringBuilder sb = new StringBuilder();
		sb.append('[').append(key.firstKey);
		if (key2 != null) {
			sb.append(',').append(key2);
		}
		if (name != null) {
			sb.append(',').append(name);
		}
		sb.append(']');
		sb.append(getMsgInfo());
		return sb.toString();
	}

	public PairKey<Object, Object> getKey() {
		return key;
	}
}
