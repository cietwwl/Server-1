package com.rw.trace.stat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.rw.fsutil.common.LongPairValue;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.common.PairKey;
import com.rwproto.MsgDef.Command;

public class MsgStatCollector {

	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> runCostContainter;
	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> sendCostContainter;
	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> submitCostContainter;
	private final ConcurrentHashMap<PairKey<Command, Object>, AtomicLong> sendMsgTimesContainer;

	public MsgStatCollector() {
		this.runCostContainter = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
		this.sendCostContainter = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
		this.submitCostContainter = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
		this.sendMsgTimesContainer = new ConcurrentHashMap<PairKey<Command, Object>, AtomicLong>();
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

	public void recordSendMsg(Command command, Object subCommand) {
		PairKey<Command, Object> key = new PairKey<Command, Object>(command, subCommand);
		AtomicLong times = this.sendMsgTimesContainer.get(key);
		if (times == null) {
			times = new AtomicLong();
			AtomicLong old = this.sendMsgTimesContainer.putIfAbsent(key, times);
			if (old != null) {
				times = old;
			}
		}
		times.incrementAndGet();
	}

	private void addCost(String name, ConcurrentHashMap<PairKey<Object, Object>, MsgStat> containter, Object msg, Object type, long cost) {
		PairKey<Object, Object> key = new PairKey<Object, Object>(msg, type);
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

	public Enumeration<MsgStat> getSubmitCostContainter() {
		return submitCostContainter.elements();
	}

	public Enumeration<MsgStat> getRunCostContainter() {
		return runCostContainter.elements();
	}

	public Enumeration<MsgStat> getSendCostContainter() {
		return sendCostContainter.elements();
	}
	
	public List<LongPairValue<PairKey<Command, Object>>> getSendMsgTimesStat(){
		ArrayList<LongPairValue<PairKey<Command, Object>>> list = new ArrayList<LongPairValue<PairKey<Command,Object>>>(sendMsgTimesContainer.size());
		for(Map.Entry<PairKey<Command, Object>, AtomicLong> entry:sendMsgTimesContainer.entrySet()){
			list.add(new LongPairValue<PairKey<Command,Object>>(entry.getKey(), entry.getValue().get()));
		}
		return list;
	}

}
