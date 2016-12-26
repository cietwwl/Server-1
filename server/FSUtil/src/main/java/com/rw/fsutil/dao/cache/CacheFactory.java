package com.rw.fsutil.dao.cache;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.rw.fsutil.common.PairValue;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.dao.cache.trace.CacheWriter;
import com.rw.fsutil.dao.cache.trace.CacheWriterImpl;
import com.rw.fsutil.dao.optimize.CacheStackTraceRecord;
import com.rw.fsutil.record.SimpleRecordService;

public class CacheFactory {

	private static final ReentrantLock lock = new ReentrantLock();
	private static final HashMap<String, CacheLogger> map = new HashMap<String, CacheLogger>();
	private static Executor executor;
	private static CacheStackTraceMap traceMap;
	public static final String LINE_SEPARATOR;
	private static CacheWriter cacheWriter;
	private static CacheStackTraceRecord record;
	private static String datalogsPath;

	static {
		try {
			datalogsPath = new File("").getCanonicalPath() + "/datalogs";
			File dirFile = new File(datalogsPath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
		executor = Executors.newFixedThreadPool(10, new SimpleThreadFactory("datalogs"));
		traceMap = new CacheStackTraceMap(4096, "trace", executor);
		LINE_SEPARATOR = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));
		cacheWriter = new CacheWriterImpl(traceMap);
		File statFile = new File(datalogsPath + "/stat");
		if (!statFile.exists()) {
			statFile.mkdir();
		}
		record = new CacheStackTraceRecord(datalogsPath, "stat");
		SimpleRecordService.getRecrodExectuor().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				record.record();
			}
		}, 2, 2, TimeUnit.MINUTES);
	}

	public static CacheLogger getLogger(String name) {
		lock.lock();
		try {
			CacheLogger logger = map.get(name);
			if (logger != null) {
				return logger;
			}
			logger = new CacheLogger(name, executor, traceMap);
			map.put(name, logger);
			return logger;
		} finally {
			lock.unlock();
		}
	}

	private static ThreadLocal<SimpleDateFormat> formatterLocal = new ThreadLocal<SimpleDateFormat>();

	public static SimpleDateFormat getFormatter() {
		SimpleDateFormat format_ = formatterLocal.get();
		if (format_ == null) {
			// format_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			format_ = new SimpleDateFormat("HH:mm:ss.SSS");
			formatterLocal.set(format_);
		}
		return format_;
	}

	public static String getStackTrace(CacheStackTrace trace) {
		CacheStackTraceEntity entity = new CacheStackTraceEntity(trace.getStackTrace(), 1);
		return traceMap.getMatch(entity);
	}

	public static CacheWriter getCacheWriter() {
		return cacheWriter;
	}

	public static List<PairValue<String, AtomicLong>> getStackTrace() {
		return traceMap.getStackTrace();
	}

	public static String getDatalogsPath() {
		return datalogsPath;
	}
}
