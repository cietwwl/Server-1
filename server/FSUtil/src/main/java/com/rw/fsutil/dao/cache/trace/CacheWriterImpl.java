package com.rw.fsutil.dao.cache.trace;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import com.rw.fsutil.dao.cache.CacheFactory;
import com.rw.fsutil.dao.cache.CacheStackTraceEntity;
import com.rw.fsutil.dao.cache.CacheStackTraceMap;
import com.rw.fsutil.dao.cache.record.LoggerWriteEvent;

public class CacheWriterImpl implements CacheWriter {

	public final String lineSeparator; // 换号符
	private final String separator;
	private final CacheStackTraceMap traceMap; // 缓存方法映射

	public CacheWriterImpl(CacheStackTraceMap traceMap) {
		this.lineSeparator = CacheFactory.LINE_SEPARATOR;
		this.traceMap = traceMap;
		this.separator = "  ";
	}

	@Override
	public void convert(LoggerEvent event,CharArrayBuffer sb) {
		LoggerWriteEvent cacheValueRecord = event.aysnEvent;
//		StringBuilder sb = new StringBuilder(cacheValueRecord != null ? 1024 : 128);
		sb.append(CacheFactory.getFormatter().format(new Date(event.time))).append(this.separator);
		sb.append(event.priority).append(this.separator);
		sb.append(event.threadName);
		if (event.trace != null) {
			StackTraceElement[] trace_ = event.trace.getStackTrace();
			String methodTraceName = traceMap.getMatch(new CacheStackTraceEntity(trace_, 1));
			sb.append(this.separator).append(methodTraceName);
		}
		if (event.content != null) {
			sb.append("|").append(event.content);
		}
		if (cacheValueRecord != null) {
			cacheValueRecord.write(sb);
		}
		sb.append(lineSeparator);
		// 异常输出需要优化
		if (event.t != null) {
			render(event.t, sb);
		}
	}

	public void render(final Throwable throwable, CharArrayBuffer sb) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			throwable.printStackTrace(pw);
		} catch (RuntimeException ex) {
		}
		pw.flush();
		LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
		try {
			String line = reader.readLine();
			while (line != null) {
				sb.append(line).append(lineSeparator);
				line = reader.readLine();
			}
		} catch (IOException ex) {
			if (ex instanceof InterruptedIOException) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
