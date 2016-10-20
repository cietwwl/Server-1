package com.rw.trace.stat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import com.rw.fsutil.common.FastPair;
import com.rw.fsutil.common.LongPair;
import com.rw.fsutil.dao.cache.CacheLogger;
import com.rw.netty.UserChannelMgr;
import com.rwproto.MsgDef.Command;

public class PrintMsgStat implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		sb.append(format.format(new Date())).append(CacheLogger.lineSeparator);
		print(sb, MsgStatFactory.getCollector().getSubmitCostContainter());
		print(sb, MsgStatFactory.getCollector().getSendCostContainter());
		print(sb, MsgStatFactory.getCollector().getRunCostContainter());
		print_(sb, UserChannelMgr.getPurgeCount());
		sb.append("===============================================").append(CacheLogger.lineSeparator);
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("stat.log", true))), true);
		try {
			writer.append(sb.toString());
		} finally {
			writer.flush();
			writer.close();
		}
		return null;
	}

	private Comparator<LongPair<MsgStat>> comparator = new Comparator<LongPair<MsgStat>>() {

		@Override
		public int compare(LongPair<MsgStat> o1, LongPair<MsgStat> o2) {
			long distance = o1.value - o2.value;
			if (distance < 0) {
				return -1;
			} else if (distance > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	private void print(StringBuilder sb, Enumeration<MsgStat> stat) {
		ArrayList<LongPair<MsgStat>> list = new ArrayList<LongPair<MsgStat>>();
		for (; stat.hasMoreElements();) {
			MsgStat msgStat = stat.nextElement();
			list.add(new LongPair<MsgStat>(msgStat, msgStat.getTotal() / msgStat.getTimes()));
		}
		Collections.sort(list, comparator);
		for (int i = 0, size = list.size(); i < size; i++) {
			MsgStat msgStat = list.get(i).t;
			sb.append(msgStat.toString()).append(CacheLogger.lineSeparator);
		}
	}

	private void print_(StringBuilder sb, Enumeration<FastPair<Command, AtomicLong>> er) {
		for (; er.hasMoreElements();) {
			FastPair<Command, AtomicLong> pair = er.nextElement();
			sb.append(pair.firstValue).append(pair.secondValue).append(CacheLogger.lineSeparator);
		}
	}

}
