package com.rw.trace.stat;

import java.util.concurrent.ConcurrentHashMap;
import com.google.protobuf.ProtocolMessageEnum;
import com.rw.fsutil.common.Pair;
import com.rwproto.MsgDef.Command;

public class MsgStatCollector {

	private final ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> runCostContainter;
	private final ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> sendCostContainter;
	private final ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> submitCostContainter;

	public MsgStatCollector() {
		this.runCostContainter = new ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat>();
		this.sendCostContainter = new ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat>();
		this.submitCostContainter = new ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat>();
	}

	public void addSendCost(Command msg, ProtocolMessageEnum type, long cost) {
		addCost("send", sendCostContainter, msg, type, cost);
	}

	public void addRunCost(Command msg, ProtocolMessageEnum type, long cost) {
		addCost("run", runCostContainter, msg, type, cost);
	}

	public void addSubmitCost(Command msg, ProtocolMessageEnum type, long cost) {
		addCost("submit", submitCostContainter, msg, type, cost);
	}

	private void addCost(String name, ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> containter, Command msg, ProtocolMessageEnum type, long cost) {
		Pair<Command, ProtocolMessageEnum> key = Pair.Create(msg, type);
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

	public ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> getSubmitCostContainter() {
		return submitCostContainter;
	}

	public ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> getRunCostContainter() {
		return runCostContainter;
	}

	public ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> getSendCostContainter() {
		return sendCostContainter;
	}

}
