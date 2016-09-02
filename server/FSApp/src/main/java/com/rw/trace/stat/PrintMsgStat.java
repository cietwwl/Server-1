package com.rw.trace.stat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;

import com.rw.fsutil.dao.cache.CacheLogger;

public class PrintMsgStat implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		sb.append(format.format(new Date())).append(CacheLogger.lineSeparator);
		print(sb, MsgStatFactory.getCollector().getSubmitCostContainter().values());
		print(sb, MsgStatFactory.getCollector().getSendCostContainter().values());
		print(sb, MsgStatFactory.getCollector().getRunCostContainter().values());
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

	private void print(StringBuilder sb, Collection<MsgStat> stat) {
		for (MsgStat msgStat : stat) {
			sb.append(msgStat.toString()).append(CacheLogger.lineSeparator);
		}
	}
}
