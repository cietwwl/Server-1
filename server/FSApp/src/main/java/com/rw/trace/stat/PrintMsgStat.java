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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import com.rw.fsutil.common.LongPairValue;
import com.rw.fsutil.common.PairKey;
import com.rw.fsutil.common.PairValue;
import com.rw.fsutil.dao.cache.CacheLogger;
import com.rw.netty.UserChannelMgr;
import com.rwproto.MsgDef.Command;

public class PrintMsgStat implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		MsgStatCollector collector = MsgStatFactory.getCollector();
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		sb.append("=======================").append(format.format(new Date())).append("=======================").append(CacheLogger.lineSeparator);
		sb.append("---------------------send success times---------------------------").append(CacheLogger.lineSeparator);
		print(sb, collector.getSendSuccessTimes());
		sb.append("-----------------------send fail times-------------------------").append(CacheLogger.lineSeparator);
		print(sb, collector.getSendFailTimes());
		sb.append("-------------------------send cost-----------------------").append(CacheLogger.lineSeparator);
		print(sb, collector.getSubmitCostContainter());
		print(sb, collector.getSendCostContainter());
		print(sb, collector.getRunCostContainter());
		sb.append("-------------------------Mgs size-----------------------").append(CacheLogger.lineSeparator);
		print(sb, collector.getMsgSizeContainter());
		sb.append("-------------------------Mgs Body size-----------------------").append(CacheLogger.lineSeparator);
		print(sb, collector.getMsgBodySizeContainter());
		sb.append("-------------------------Data Sync size-----------------------").append(CacheLogger.lineSeparator);
		print(sb, collector.getDataSyncSizeContainter());
		sb.append("------------------------purge msg------------------------").append(CacheLogger.lineSeparator);
		print_(sb, UserChannelMgr.getPurgeCount());
		sb.append("===============================================================").append(CacheLogger.lineSeparator).append(CacheLogger.lineSeparator);
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("stat.log", true))), true);
		try {
			writer.append(sb.toString());
		} finally {
			writer.flush();
			writer.close();
		}
		return null;
	}

	private Comparator<LongPairValue<MsgStat>> comparator = new Comparator<LongPairValue<MsgStat>>() {

		@Override
		public int compare(LongPairValue<MsgStat> o1, LongPairValue<MsgStat> o2) {
			long distance = o1.value - o2.value;
			if (distance < 0) {
				return 1;
			} else if (distance > 0) {
				return -1;
			} else {
				return 0;
			}
		}
	};

	private Comparator<LongPairValue<PairKey<Command, Object>>> timesComparator = new Comparator<LongPairValue<PairKey<Command, Object>>>() {

		@Override
		public int compare(LongPairValue<PairKey<Command, Object>> o1, LongPairValue<PairKey<Command, Object>> o2) {
			if (o1.value < o2.value) {
				return 1;
			} else if (o1.value > o2.value) {
				return -1;
			} else {
				return 0;
			}
		}

	};

	private void print(StringBuilder sb, List<LongPairValue<PairKey<Command, Object>>> list) {
		Collections.sort(list, timesComparator);
		for (int i = 0, size = list.size(); i < size; i++) {
			LongPairValue<PairKey<Command, Object>> msgStat = list.get(i);
			sb.append(msgStat.t).append(" = ").append(msgStat.value).append(CacheLogger.lineSeparator);
		}
	}

	private void print(StringBuilder sb, Enumeration<MsgStat> stat) {
		ArrayList<LongPairValue<MsgStat>> list = new ArrayList<LongPairValue<MsgStat>>();
		for (; stat.hasMoreElements();) {
			MsgStat msgStat = stat.nextElement();
			list.add(new LongPairValue<MsgStat>(msgStat, msgStat.getTotal() / msgStat.getTimes()));
		}
		Collections.sort(list, comparator);
		for (int i = 0, size = list.size(); i < size; i++) {
			MsgStat msgStat = list.get(i).t;
			sb.append(msgStat.toString()).append(CacheLogger.lineSeparator);
		}
	}

	private void print_(StringBuilder sb, Enumeration<PairValue<Command, AtomicLong>> er) {
		for (; er.hasMoreElements();) {
			PairValue<Command, AtomicLong> pair = er.nextElement();
			sb.append(pair.firstValue).append(" = ").append(pair.secondValue).append(CacheLogger.lineSeparator);
		}
	}
}
