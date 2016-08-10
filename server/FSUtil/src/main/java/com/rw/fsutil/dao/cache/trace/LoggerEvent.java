package com.rw.fsutil.dao.cache.trace;

import com.rw.fsutil.dao.cache.CacheLoggerPriority;
import com.rw.fsutil.dao.cache.CacheStackTrace;
import com.rw.fsutil.dao.cache.record.LoggerWriteEvent;

public class LoggerEvent {

	public final CacheLoggerPriority priority;
	public final String content;
	public final LoggerWriteEvent aysnEvent;
	public final Throwable t;
	public final long time;
	public final String threadName;
	public final CacheStackTrace trace;

	public LoggerEvent(CacheLoggerPriority priority, String str, Throwable t) {
		super();
		this.priority = priority;
		this.content = str;
		this.t = t;
		this.time = System.currentTimeMillis();
		Thread thread = Thread.currentThread();
		this.threadName = thread.getName();
		this.trace = null;
		this.aysnEvent = null;
	}

	public LoggerEvent(CacheLoggerPriority priority, String str, Throwable t, boolean test) {
		super();
		this.priority = priority;
		this.content = str;
		this.t = t;
		this.time = System.currentTimeMillis();
		this.threadName = Thread.currentThread().getName();
		if (test) {
			this.trace = new CacheStackTrace();
		} else {
			this.trace = null;
		}
		this.aysnEvent = null;
	}

	public LoggerEvent(CacheLoggerPriority priority, LoggerWriteEvent aysnEvent, String content, CacheStackTrace trace) {
		this.priority = priority;
		this.content = content;
		this.t = null;
		this.time = System.currentTimeMillis();
		Thread thread = Thread.currentThread();
		this.threadName = thread.getName();
		this.trace = trace;
		this.aysnEvent = aysnEvent;
	}

	@Override
	public String toString() {
		return content + "[" + threadName + "]";
	}

}
