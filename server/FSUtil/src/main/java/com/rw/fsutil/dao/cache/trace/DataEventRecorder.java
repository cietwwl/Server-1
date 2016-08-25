package com.rw.fsutil.dao.cache.trace;


public class DataEventRecorder {

	private static ThreadLocal<DataEventCollector> local = new ThreadLocal<DataEventCollector>();

	public static void startDataEventCollect(Object param) {
		DataEventCollector collector = local.get();
		if (collector == null) {
			collector = new DataEventCollector();
		} else if (collector.startCollect) {
			throw new IllegalStateException("has start collect");
		}
		collector.startCollect = true;
		collector.param = param;
		local.set(collector);
	}

	public static Object getParam() {
		DataEventCollector collector = local.get();
		if (collector == null) {
			throw new IllegalStateException("has start collect");
		}
		if (!collector.startCollect) {
			throw new IllegalStateException("has start collect");
		}
		return collector.param;
	}

	public static Entry endAndPollCollections() {
		DataEventCollector collector = local.get();
		if (collector == null) {
			throw new IllegalStateException("collector not init");
		}
		if (!collector.startCollect) {
			throw new IllegalStateException("not start collect");
		}
		Object param = collector.param;
		collector.param = null;
		return new Entry(param);
	}

	static class DataEventCollector {
		// 开始标志
		boolean startCollect;
		// 参数
		Object param;

	}

	// 封装结果集
	public static class Entry {

		final Object param;

		public Object getParam() {
			return param;
		}

		public Entry(Object param) {
			this.param = param;
		}

	}
}
