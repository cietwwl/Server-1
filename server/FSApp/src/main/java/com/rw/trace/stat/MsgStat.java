package com.rw.trace.stat;

import com.google.protobuf.ProtocolMessageEnum;
import com.rw.fsutil.common.Pair;
import com.rwproto.MsgDef.Command;

public class MsgStat {

	private final Pair<Command, ProtocolMessageEnum> key;
	private final String name;
	private int total;
	private int times;
	private final int recentlyMax;
	private final int maxIndex;
	private int recentlyCount;
	private int[] recentlyData;
	private int index;
	private int max;

	public MsgStat(String name, Pair<Command, ProtocolMessageEnum> key, int recentlyMax) {
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

	@Override
	public String toString() {
		return "[" + key.getT1() + "," + key.getT2() + "," + name + "]" + getMsgInfo();
	}

	public Pair<Command, ProtocolMessageEnum> getKey() {
		return key;
	}
}
