package com.rw.trace.stat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.rw.fsutil.common.LongPairValue;
import com.rw.fsutil.common.PairKey;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.MsgDef.Command;

/**
 * 消息信息统计收集器
 * @author Jamaz
 *
 */
public class MsgStatCollector {

	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> runCostContainter;
	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> sendCostContainter;
	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> submitCostContainter;
	private final ConcurrentHashMap<PairKey<Command, Object>, AtomicLong> sendMsgTimesContainer;
	private final ConcurrentHashMap<PairKey<Command, Object>, AtomicLong> sendMsgFailTimesContainer;
	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> msgSizeContainer;
	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> msgBodySizeContainer;
	private final ConcurrentHashMap<PairKey<Object, Object>, MsgStat> mergeDataSynContainer;

	public MsgStatCollector() {
		this.runCostContainter = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
		this.sendCostContainter = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
		this.submitCostContainter = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
		this.sendMsgTimesContainer = new ConcurrentHashMap<PairKey<Command, Object>, AtomicLong>();
		this.sendMsgFailTimesContainer = new ConcurrentHashMap<PairKey<Command, Object>, AtomicLong>();
		this.msgSizeContainer = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
		this.msgBodySizeContainer = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
		this.mergeDataSynContainer = new ConcurrentHashMap<PairKey<Object, Object>, MsgStat>();
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

	public void recordDataSynSize(eSynType key, int size, Object subKey) {
		addCost(null, mergeDataSynContainer, key, subKey, size);
	}

	public void recordSendFailMsg(Command command, Object subCommand) {
		recordSendMsg(command, subCommand, this.sendMsgFailTimesContainer);
	}

	public void recordSendSuccessMsg(Command command, Object subCommand, int headSize, int bodySize) {
		recordSendMsg(command, subCommand, this.sendMsgTimesContainer);
		addCost(null, msgSizeContainer, command, subCommand, headSize + bodySize);
		addCost(null, msgBodySizeContainer, command, subCommand, bodySize);
	}

	private void recordSendMsg(Command command, Object subCommand, ConcurrentHashMap<PairKey<Command, Object>, AtomicLong> container) {
		PairKey<Command, Object> key = new PairKey<Command, Object>(command, subCommand);
		AtomicLong times = container.get(key);
		if (times == null) {
			times = new AtomicLong();
			AtomicLong old = container.putIfAbsent(key, times);
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

	public Enumeration<MsgStat> getMsgSizeContainter() {
		return msgSizeContainer.elements();
	}

	public Enumeration<MsgStat> getMsgBodySizeContainter() {
		return msgBodySizeContainer.elements();
	}

	public Enumeration<MsgStat> getDataSyncSizeContainter() {
		return mergeDataSynContainer.elements();
	}

	public List<LongPairValue<PairKey<Command, Object>>> getSendFailTimes() {
		return getSendMsgTimesStat(this.sendMsgFailTimesContainer);
	}

	public List<LongPairValue<PairKey<Command, Object>>> getSendSuccessTimes() {
		return getSendMsgTimesStat(this.sendMsgTimesContainer);
	}

	private List<LongPairValue<PairKey<Command, Object>>> getSendMsgTimesStat(ConcurrentHashMap<PairKey<Command, Object>, AtomicLong> container) {
		ArrayList<LongPairValue<PairKey<Command, Object>>> list = new ArrayList<LongPairValue<PairKey<Command, Object>>>(container.size());
		for (Map.Entry<PairKey<Command, Object>, AtomicLong> entry : container.entrySet()) {
			list.add(new LongPairValue<PairKey<Command, Object>>(entry.getKey(), entry.getValue().get()));
		}
		return list;
	}

}
