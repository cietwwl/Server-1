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

import com.rw.fsutil.dao.cache.CacheLogger;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.TableUpdateContainer;
import com.rw.fsutil.dao.optimize.UpdateCountStat;

public class PrintTableStat implements Callable<Void> {

	@Override
	public Void call() throws Exception {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		sb.append(format.format(new Date())).append(CacheLogger.lineSeparator);
		// for (MsgStat msgStat :
		// MsgStatFactory.getCollector().getContainer().values()) {
		// sb.append(msgStat.toString()).append(CacheLogger.lineSeparator);
		// }
		ArrayList<UpdateCountStat> sortList = new ArrayList<UpdateCountStat>();
		Enumeration<TableUpdateContainer<Object, Object>> enumeration = DataAccessFactory.getTableUpdateCollector().getAllContainer();
		for (; enumeration.hasMoreElements();) {
			TableUpdateContainer<Object, Object> container = enumeration.nextElement();
			// String name = container.getTableName();
			// print(sb, name, container.getUpdateStat());
			// print(sb, name, container.getEvictStat());
			sortList.add(container.getUpdateStat());
			sortList.add(container.getEvictStat());
		}
		Collections.sort(sortList, new Comparator<UpdateCountStat>() {

			@Override
			public int compare(UpdateCountStat o1, UpdateCountStat o2) {
				return o2.getTotalCost() - o1.getTotalCost();
			}
		});
		for (int i = 0; i < sortList.size(); i++) {
			UpdateCountStat stat = sortList.get(i);
			print(sb, stat.getName(), stat);
		}
		sb.append("===============================================").append(CacheLogger.lineSeparator);
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("table.log", true))), true);
		try {
			writer.append(sb.toString());
		} finally {
			writer.flush();
			writer.close();
		}
		return null;
	}

	private void print(StringBuilder sb, String name, UpdateCountStat updateStat) {
		sb.append(name).append('=').append(updateStat.toString()).append(CacheLogger.lineSeparator);
	}

}
