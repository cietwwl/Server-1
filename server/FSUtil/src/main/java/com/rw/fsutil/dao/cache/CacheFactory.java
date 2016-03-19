package com.rw.fsutil.dao.cache;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class CacheFactory {

	private static final ReentrantLock lock = new ReentrantLock();
	private static final HashMap<String, CacheLogger> map = new HashMap<String, CacheLogger>();
	private static Executor executor;
	private static CacheStackTraceMap traceMap;
	public static final String LINE_SEPARATOR;

	static {
		executor = Executors.newFixedThreadPool(20, new SimpleThreadFactory("logger factory"));
		traceMap = new CacheStackTraceMap(1000, "trace", executor);
		LINE_SEPARATOR = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

	}

	public static CacheLogger getLogger(Class clazz) {
		return getLogger(clazz.getSimpleName());
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
			format_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			formatterLocal.set(format_);
		}
		return format_;
	}

}
