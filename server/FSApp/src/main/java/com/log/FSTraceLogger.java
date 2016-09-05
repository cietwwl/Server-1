package com.log;

import com.google.protobuf.ProtocolMessageEnum;
import com.rw.manager.ServerSwitch;
import com.rw.trace.stat.MsgStatCollector;
import com.rw.trace.stat.MsgStatFactory;
import com.rwproto.MsgDef.Command;

public class FSTraceLogger {

	public static void logger(String text) {
		if (ServerSwitch.isOpenTraceLogger()) {
			System.out.println(text);
		}
	}

	public static void logger(String head, long cost, Command command, long seqId, String userId) {
		logger(head, cost, command, null, seqId, userId, null);
	}

	public static void logger(String head, long cost, String command, long seqId, String userId, String account, boolean record) {
		logger(head, cost, command, null, seqId, userId, account);
		if (record) {
			MsgStatFactory.getCollector().addRunCost(command, null, cost);
		}
	}
	
	public static void recordRun(String command,long cost){
		MsgStatFactory.getCollector().addRunCost(command, null, cost);
	}

	public static void logger(String head, long cost, Command command, ProtocolMessageEnum type, long seqId, String userId) {
		logger(head, cost, command, type, seqId, userId, null);
		if (type != null) {
			MsgStatFactory.getCollector().addRunCost(command, type, cost);
		}
	}

	// 后面再整合
	public static void loggerSendAndSubmit(String head, long submitCost, long sendCost, Object command, Object type, long seqId, String userId, String account) {
		if (!ServerSwitch.isOpenTraceLogger()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(head).append('(').append(submitCost).append(',').append(sendCost).append(',').append(command);
		if (type != null) {
			sb.append('-').append(type);
		}
		sb.append(',').append(seqId).append(")[");
		if (account != null) {
			sb.append(account).append(',');
		}
		sb.append(userId).append(']');
		logger(sb.toString());
		MsgStatCollector collector = MsgStatFactory.getCollector();
		collector.addSendCost(command, type, sendCost);
		collector.addSubmitCost(command, type, submitCost);
	}

	public static void logger(String head, long cost, Object oneKey, Object secondekey, long seqId, String userId, String account) {
		if (!ServerSwitch.isOpenTraceLogger()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(head).append('(').append(cost).append(',').append(oneKey);
		if (secondekey != null) {
			sb.append('-').append(secondekey);
		}
		sb.append(',').append(seqId).append(")[");
		if (account != null) {
			sb.append(account).append(',');
		}
		sb.append(userId).append(']');
		logger(sb.toString());
	}

}
