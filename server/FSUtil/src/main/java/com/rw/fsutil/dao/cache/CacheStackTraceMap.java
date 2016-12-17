package com.rw.fsutil.dao.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.rw.fsutil.common.PairValue;

public class CacheStackTraceMap {

	private final ReentrantLock lock;
	private final LinkedHashMap<CacheStackTraceEntity, PairValue<String, AtomicLong>> map;
	private final AtomicInteger idGenerator;
	private final String name;
	private final BufferedWriter writer;
	private final Executor executor;

	public CacheStackTraceMap(final int maxCapacity, String name, Executor executor) {
		this.name = "t" + new SimpleDateFormat("MMdd").format(new Date());
		this.executor = executor;
		this.map = new LinkedHashMap<CacheStackTraceEntity, PairValue<String, AtomicLong>>(maxCapacity >> 2, 0.5f, true) {

			protected boolean removeEldestEntry(Map.Entry<CacheStackTraceEntity, PairValue<String, AtomicLong>> eldest) {
				return size() > maxCapacity;
			}
		};
		this.lock = new ReentrantLock();
		
		this.idGenerator = new AtomicInteger();
		try {
			String firstDirPath = CacheFactory.getDatalogsPath();
			File dirFile = new File(firstDirPath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			String secondDirPath = firstDirPath + "/methodtrace";
			dirFile = new File(secondDirPath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			this.writer = new BufferedWriter(new FileWriter(new File(secondDirPath + "/" + this.name + ".log"), true));
		} catch (Exception ex) {
			throw new ExceptionInInitializerError();
		}

	}

	public String getMatch(final CacheStackTraceEntity trace) {
		if (trace == null) {
			return "";
		}
		AtomicLong countStat = null;
		lock.lock();
		try {
			PairValue<String, AtomicLong> oldName = map.get(trace);
			if (oldName != null) {
				countStat = oldName.secondValue;
				return oldName.firstValue;
			}
			final String creator = name + "_" + idGenerator.incrementAndGet();
			map.put(trace, new PairValue<String, AtomicLong>(creator, new AtomicLong(1)));
			// 创建一个method记录
			executor.execute(new Runnable() {

				@Override
				public void run() {
					StringBuilder sb = new StringBuilder();
					sb.append(CacheFactory.getFormatter().format(new Date()));
					sb.append("   ");
					sb.append("create new mehtod trace： " + creator).append(CacheFactory.LINE_SEPARATOR);
					if (trace != null) {
						trace.fill(sb);
					}
					String info = sb.toString();
					synchronized (writer) {
						try {
							writer.write(info);
							writer.flush();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			});
			return creator;
		} finally {
			lock.unlock();
			if (countStat != null) {
				countStat.incrementAndGet();
			}
		}
	}

	public List<PairValue<String, AtomicLong>> getStackTrace() {
		lock.lock();
		try {
			return new ArrayList<PairValue<String, AtomicLong>>(map.values());
		} finally {
			lock.unlock();
		}

	}
}
