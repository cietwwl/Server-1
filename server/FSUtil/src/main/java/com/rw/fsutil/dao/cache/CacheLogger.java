package com.rw.fsutil.dao.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class CacheLogger {

	private final String fileName;
	private volatile BufferedWriter writer;
	public static final String lineSeparator; // 换号符
	private final Executor executor; // 异步执行处理器
	private volatile long currentTimeMillis; // 当前文件时间(日)
	private final CacheStackTraceMap traceMap; // 缓存方法映射

	static {
		lineSeparator = CacheFactory.LINE_SEPARATOR;
	}

	public CacheLogger(String name, Executor executor, CacheStackTraceMap traceMap) {
		this.executor = executor;
		this.fileName = name;
		try {
			checkWriter(System.currentTimeMillis());
		} catch (Exception e) {
			throw new ExceptionInInitializerError("初始化logger失败：" + fileName);
		}
		this.traceMap = traceMap;
	}

	public void fatal(String s) {
		this.executor.execute(new Event(CacheLoggerPriority.FATAL, s, null));
	}

	public void info(String s) {
		this.executor.execute(new Event(CacheLoggerPriority.INFO, s, null));
	}

	public void info(String s, boolean test) {
		this.executor.execute(new Event(CacheLoggerPriority.INFO, s, null, test));
	}

	public void info(String s, Throwable t) {
		this.executor.execute(new Event(CacheLoggerPriority.INFO, s, t));
	}

	public void warn(String s) {
		this.executor.execute(new Event(CacheLoggerPriority.WARN, s, null));
	}

	public void warn(String s, Throwable t) {
		this.executor.execute(new Event(CacheLoggerPriority.WARN, s, t));
	}

	public void error(String s) {
		this.executor.execute(new Event(CacheLoggerPriority.ERROR, s, null));
	}

	public void error(String s, Throwable t) {
		this.executor.execute(new Event(CacheLoggerPriority.ERROR, s, t));
	}

	class Event implements Runnable {

		private final CacheLoggerPriority priority;
		private final String str;
		private final CacheLoggerAsynEvent aysnEvent;
		private final Throwable t;
		private final long time;
		private final String threadName;
		private final CacheStackTrace trace;

		public Event(CacheLoggerPriority priority, String str, Throwable t) {
			super();
			this.priority = priority;
			this.str = str;
			this.t = t;
			this.time = System.currentTimeMillis();
			Thread thread = Thread.currentThread();
			this.threadName = thread.getName();
			this.trace = null;
			this.aysnEvent = null;
		}

		public Event(CacheLoggerPriority priority, String str, Throwable t, boolean test) {
			super();
			this.priority = priority;
			this.str = str;
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

		public Event(CacheLoggerPriority priority, CacheLoggerAsynEvent aysnEvent) {
			this.priority = priority;
			this.str = null;
			this.t = null;
			this.time = System.currentTimeMillis();
			Thread thread = Thread.currentThread();
			this.threadName = thread.getName();
			this.trace = new CacheStackTrace();
			this.aysnEvent = aysnEvent;
		}

		@Override
		public void run() {
			try {
				checkWriter(time);
				StringBuilder sb = new StringBuilder();
				sb.append(CacheFactory.getFormatter().format(new Date(time)));
				sb.append("   ");
				sb.append(priority);
				sb.append("   ");
				sb.append(threadName);
				sb.append("  ");
				if (aysnEvent != null) {
					sb.append(aysnEvent.getCurrentRecord().getLastInfo().toString());
				} else {
					sb.append(str);
				}
				sb.append(lineSeparator);
				String errString = null;
				if (trace != null) {
					// errString = sb.toString();
					StackTraceElement[] trace_ = trace.getStackTrace();
					String methodTraceName = traceMap.getMatch(new CacheStackTraceEntity(trace_, 1));
					sb.append("trace name = " + methodTraceName).append(lineSeparator);
				}
				if (t != null) {
					render(t, sb);
				}
				String logString = sb.toString();
				if (errString == null) {
					errString = logString;
				}
				synchronized (this) {
					writer.write(logString);
					writer.flush();
				}
				if (priority != CacheLoggerPriority.INFO) {
					System.err.print(errString);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public void executeAysnEvent(CacheLoggerPriority priority, CacheLoggerAsynEvent aysnEvent) {
		this.executor.execute(new Event(priority, aysnEvent));
	}

	public static void render(final Throwable throwable, StringBuilder sb) {
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

	private final static int TIMEDAY = (int) TimeUnit.DAYS.toMillis(1);// 天
	public static final long TIME_ZONE_OFFSET_MILLIS = TimeZone.getDefault().getRawOffset();

	public void checkWriter(long newTime) throws IOException {
		// 判断如果不是同一天就新建文件夹
		long oldTime = currentTimeMillis;
		long oldTimeDay = (oldTime + TIME_ZONE_OFFSET_MILLIS) / TIMEDAY;
		long newTimeDay = (newTime + TIME_ZONE_OFFSET_MILLIS) / TIMEDAY;
		if (oldTimeDay != newTimeDay) {
			String dirPath = getParentPath(newTime);
			String name = dirPath + "/" + fileName + ".log";
			try {
				synchronized (this) {
					if (currentTimeMillis != oldTime) {
						return;
					}
					BufferedWriter oldWriter = this.writer;
					this.writer = new BufferedWriter(new FileWriter(new File(name), true));
					this.currentTimeMillis = newTime;
					if (oldWriter != null) {
						try {
							oldWriter.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				throw new ExceptionInInitializerError("初始化logger失败：" + fileName);
			}
		}
	}

	// 获取上层目录路径
	private String getParentPath(long currentTimeMillis) throws IOException {
		File f = new File("");
		String firstDirPath = f.getCanonicalPath() + "/datalogs";
		String secondDirPath = firstDirPath + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date(currentTimeMillis));
		File dirFile = new File(secondDirPath);
		if (dirFile.exists()) {
			return secondDirPath;
		}
		synchronized (CacheLogger.class) {
			File firstDirFile = new File(firstDirPath);
			if (!firstDirFile.exists()) {
				boolean reuslt = firstDirFile.mkdir();
				if (!reuslt) {
					throw new IOException("创建目录失败：" + firstDirPath);
				}
			}
			File secondDirFile = new File(secondDirPath);
			if (!secondDirFile.exists()) {
				boolean reuslt = secondDirFile.mkdir();
				if (!reuslt) {
					throw new IOException("创建目录失败：" + secondDirPath);
				}
			}
		}
		return secondDirPath;
	}

}
