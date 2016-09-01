package com.rw.trace.stat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.ProtocolMessageEnum;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.CacheLogger;
import com.rwproto.MsgDef.Command;

public class MsgStatCollector implements Callable<Void> {

	private ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> containter;
	
	public MsgStatCollector() {
		this.containter = new ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat>();
	}

	public void addCost(Command msg, ProtocolMessageEnum type, long cost) {
		Pair<Command, ProtocolMessageEnum> key = Pair.Create(msg, type);
		MsgStat msgStat = containter.get(key);
		if (msgStat == null) {
			msgStat = new MsgStat(key, 100);
			MsgStat old = containter.putIfAbsent(key, msgStat);
			if (old != null) {
				msgStat = old;
			}
		}
		msgStat.add(cost);
	}
	
	public ConcurrentHashMap<Pair<Command, ProtocolMessageEnum>, MsgStat> getContainer(){
		return containter;
	}

	@Override
	public Void call() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (MsgStat msgStat : containter.values()) {
			sb.append(msgStat.toString()).append(CacheLogger.lineSeparator);
		}
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("stat.log",true))), true);
		try {
			writer.append(sb.toString());
		} finally {
			writer.flush();
			writer.close();
		}
		return null;
	}

}
