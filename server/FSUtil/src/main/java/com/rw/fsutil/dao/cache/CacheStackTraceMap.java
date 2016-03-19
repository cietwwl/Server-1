package com.rw.fsutil.dao.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class CacheStackTraceMap {

	private final ReentrantLock lock;
	private final LinkedHashMap<CacheStackTraceEntity, String> map;
	private final AtomicInteger idGenerator;
	private final String name;
	private final BufferedWriter writer;
	private final Executor executor;

	public CacheStackTraceMap(final int maxCapacity, String name, Executor executor) {
		this.name = "trace" + new SimpleDateFormat("yyyyMMdd").format(new Date());
		this.executor = executor;
		this.map = new LinkedHashMap<CacheStackTraceEntity, String>(10000, 0.5f, true) {

			protected boolean removeEldestEntry(Map.Entry<CacheStackTraceEntity, String> eldest) {
				return size() > maxCapacity;
			}
		};
		this.lock = new ReentrantLock();
		this.idGenerator = new AtomicInteger();

		File f = new File("");
		try {
			String firstDirPath = f.getCanonicalPath() + "/datalogs";
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
		lock.lock();
		try {
			String oldName = map.get(trace);
			if (oldName != null) {
				return oldName;
			}
			final String creator = name + "_" + idGenerator.incrementAndGet();
			map.put(trace, creator);
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
		}
	}

}
