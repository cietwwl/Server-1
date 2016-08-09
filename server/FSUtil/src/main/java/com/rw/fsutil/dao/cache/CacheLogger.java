package com.rw.fsutil.dao.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import com.rw.fsutil.dao.cache.record.LoggerWriteEvent;
import com.rw.fsutil.dao.cache.trace.CacheWriter;
import com.rw.fsutil.dao.cache.trace.CharArrayBuffer;
import com.rw.fsutil.dao.cache.trace.LoggerEvent;

public class CacheLogger implements Runnable {

	public static final String lineSeparator; // 换号符
	private final String fileName;
	private final Executor executor; // 异步执行处理器
	private volatile long currentTimeMillis; // 当前文件时间(日)
	// private volatile BufferedWriter writer;
	private volatile FileOutputStream output;
	private final ConcurrentLinkedQueue<LoggerEvent> queue;
	private final int flushCount = 1024 * 1024;
	private final int compressLength = 1024;
	private final int maxCapacity;
	private CharArrayBuffer charBuffer;
	private GzipTempArrayBuffer baos;
	private final Charset charSet;

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
		// use blockingqueue better
		this.queue = new ConcurrentLinkedQueue<LoggerEvent>();
		this.maxCapacity = flushCount * 2;
		this.charBuffer = new CharArrayBuffer(this.flushCount >> 2,this.maxCapacity);
		this.baos = new GzipTempArrayBuffer(this.flushCount >> 2);
		this.charSet = Charset.forName("UTF-8");
	}

	static class GzipTempArrayBuffer extends ByteArrayOutputStream {

		public GzipTempArrayBuffer(int size) {
			super(size);
		}

		public byte[] array() {
			return buf;
		}
	}

	public void fatal(String s) {
		offer(new LoggerEvent(CacheLoggerPriority.FATAL, s, null));
	}

	public void info(String s) {
		offer(new LoggerEvent(CacheLoggerPriority.INFO, s, null));
	}

	public void info(String s, boolean test) {
		offer(new LoggerEvent(CacheLoggerPriority.INFO, s, null, test));
	}

	public void info(String s, Throwable t) {
		offer(new LoggerEvent(CacheLoggerPriority.INFO, s, t));
	}

	public void warn(String s) {
		offer(new LoggerEvent(CacheLoggerPriority.WARN, s, null));
	}

	public void warn(String s, Throwable t) {
		offer(new LoggerEvent(CacheLoggerPriority.WARN, s, t));
	}

	public void error(String s) {
		offer(new LoggerEvent(CacheLoggerPriority.ERROR, s, null));
	}

	public void error(String s, Throwable t) {
		offer(new LoggerEvent(CacheLoggerPriority.ERROR, s, t));
	}

	private boolean processing;

	private void offer(LoggerEvent event) {
		this.queue.offer(event);
		boolean submit;
		synchronized (this) {
			if (processing) {
				submit = false;
			} else {
				processing = true;
				submit = true;
			}
		}
		if (submit) {
			this.executor.execute(this);
		}
	}

	@Override
	public void run() {
		CacheWriter cacheWriter = CacheFactory.getCacheWriter();
		int count = 0;
		for (;;) {
			try {
				LoggerEvent event = this.queue.poll();
				if (event == null) {
					if (count > 0) {
						count = 0;
						int size = charBuffer.size();
						if (size < compressLength) {
							write();
						} else {
							writeWithCompress();
						}
					}
					synchronized (this) {
						event = this.queue.poll();
						if (event == null) {
							processing = false;
							break;
						}
					}
				}
				count++;
				checkWriter(event.time);
				cacheWriter.convert(event, charBuffer);
				if (charBuffer.size() >= flushCount) {
					writeWithCompress();
					cacheWriter = CacheFactory.getCacheWriter();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private void write() {
		try {
			CharBuffer buffer = CharBuffer.wrap(charBuffer.getValue(), 0, charBuffer.size());
			ByteBuffer byteBuffer = charSet.encode(buffer);
			write((byte) 0, byteBuffer.array(), byteBuffer.limit());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			charBuffer.clear();
		}
	}

	private void write(byte flag, byte[] org, int len) throws IOException {
		byte[] array = new byte[len + 5];
		array[0] = (byte) ((len >>> 24) & 0xff);
		array[1] = (byte) ((len >>> 16) & 0xff);
		array[2] = (byte) ((len >>> 8) & 0xff);
		array[3] = (byte) (len & 0xff);
		array[4] = flag;
		System.arraycopy(org, 0, array, 5, len);
		output.write(array);
		output.flush();
	}

	private void writeWithCompress() {
		try {
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			CharBuffer buffer = CharBuffer.wrap(charBuffer.getValue(), 0, charBuffer.size());
			ByteBuffer byteBuffer = charSet.encode(buffer);
			gos.write(byteBuffer.array(), 0, byteBuffer.limit());
			gos.close();
			write((byte) 1, baos.array(), baos.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			charBuffer.clear();
			if (baos.array().length > maxCapacity) {
				baos = new GzipTempArrayBuffer(maxCapacity);
			} else {
				baos.reset();
			}
		}
	}

	public void executeAysnEvent(CacheLoggerPriority priority, String content, LoggerWriteEvent aysnEvent, CacheStackTrace trace) {
		offer(new LoggerEvent(priority, aysnEvent, content, trace));
	}

	private final static long DAY_MILLIS = TimeUnit.DAYS.toMillis(1);// 天
	public static final long TIME_ZONE_OFFSET = TimeZone.getDefault().getRawOffset();

	public void checkWriter(long newTime) throws IOException {
		// 判断如果不是同一天就新建文件夹
		long oldTime = currentTimeMillis;
		long oldTimeDay = (oldTime + TIME_ZONE_OFFSET) / DAY_MILLIS;
		long newTimeDay = (newTime + TIME_ZONE_OFFSET) / DAY_MILLIS;
		if (oldTimeDay != newTimeDay) {
			String dirPath = getParentPath(newTime);
			String name = dirPath + "/" + fileName + ".log";
			try {
				if (currentTimeMillis != oldTime) {
					return;
				}
				FileOutputStream oldOutput = this.output;
				this.output = new FileOutputStream(new File(name), true);
				this.currentTimeMillis = newTime;
				if (oldOutput != null) {
					try {
						oldOutput.flush();
						oldOutput.close();
					} catch (IOException ex) {
						ex.printStackTrace();
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
		// TODO 可优化
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
