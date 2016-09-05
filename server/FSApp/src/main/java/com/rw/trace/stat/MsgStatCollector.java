package com.rw.trace.stat;

import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.common.Pair;

public class MsgStatCollector {

	private final ConcurrentHashMap<Pair<Object, Object>, MsgStat> runCostContainter;
	private final ConcurrentHashMap<Pair<Object, Object>, MsgStat> sendCostContainter;
	private final ConcurrentHashMap<Pair<Object, Object>, MsgStat> submitCostContainter;

	public MsgStatCollector() {
		this.runCostContainter = new ConcurrentHashMap<Pair<Object, Object>, MsgStat>();
		this.sendCostContainter = new ConcurrentHashMap<Pair<Object, Object>, MsgStat>();
		this.submitCostContainter = new ConcurrentHashMap<Pair<Object, Object>, MsgStat>();
	}

	public void addSendCost(Object msg, Object type, long cost) {
		addCost("send", sendCostContainter, msg, type, cost);
	}

	public void addRunCost(Object msg, Object type, long cost) {
		addCost("run", runCostContainter, msg, type, cost);
	}

	public void addSubmitCost(Object msg, Object type, long cost) {
		addCost("submit", submitCostContainter, msg, type, cost);
	}

	private void addCost(String name, ConcurrentHashMap<Pair<Object, Object>, MsgStat> containter, Object msg, Object type, long cost) {
		Pair<Object, Object> key = Pair.Create(msg, type);
		MsgStat msgStat = containter.get(key);
		if (msgStat == null) {
			msgStat = new MsgStat(name, key, 100);
			MsgStat old = containter.putIfAbsent(key, msgStat);
			if (old != null) {
				msgStat = old;
			}
		}
		msgStat.add(cost);
	}

	public ConcurrentHashMap<Pair<Object, Object>, MsgStat> getSubmitCostContainter() {
		return submitCostContainter;
	}

	public ConcurrentHashMap<Pair<Object, Object>, MsgStat> getRunCostContainter() {
		return runCostContainter;
	}

	public ConcurrentHashMap<Pair<Object, Object>, MsgStat> getSendCostContainter() {
		return sendCostContainter;
	}

}
