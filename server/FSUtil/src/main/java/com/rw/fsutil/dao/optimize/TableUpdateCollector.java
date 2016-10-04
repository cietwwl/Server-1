package com.rw.fsutil.dao.optimize;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.fsutil.concurrent.QueuedTaskExecutor;
import com.rw.fsutil.dao.cache.evict.EvictedUpdateTask;
import com.rw.fsutil.log.EngineLogger;
import com.rw.fsutil.log.EngineLoggerFactory;

public class TableUpdateCollector implements Runnable {

	private HashMap<String, String> tableSqlMapper;
	private ConcurrentHashMap<String, TableUpdateContainer<Object, Object>> containerMap;
	private QueuedTaskExecutor<String, Void> taskExecutor;
	private final int periodMillis;

	public TableUpdateCollector() {
		this.tableSqlMapper = new HashMap<String, String>();
		this.containerMap = new ConcurrentHashMap<String, TableUpdateContainer<Object, Object>>();
		ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1, new SimpleThreadFactory("db-period"));
		schedule.scheduleAtFixedRate(this, 2, 2, TimeUnit.SECONDS);
		EngineLogger logger = EngineLoggerFactory.getLogger("db");
		ExecutorService executor = Executors.newFixedThreadPool(8, new SimpleThreadFactory("db"));
		this.periodMillis = 1000;
		this.taskExecutor = new QueuedTaskExecutor<String, Void>(8, logger, executor) {
		};
	}

	public <K> void add(String tableName, long periodMillis, K compositeKey, PersistentParamsExtractor<K> extractor) {
		TableUpdateContainer<Object, Object> wrap = getTableUpdateWrap(tableName);
		if (wrap == null) {
			FSUtilLogger.error("update fail cause by table not exist:" + tableName + ",key=" + compositeKey);
			return;
		}
		wrap.addChanged(compositeKey, periodMillis, (PersistentParamsExtractor<Object>) extractor);
	}

	private TableUpdateContainer<Object, Object> getTableUpdateWrap(String tableName) {
		TableUpdateContainer<Object, Object> wrap = containerMap.get(tableName);
		if (wrap == null) {
			String sql = tableSqlMapper.get(tableName);
			if (sql == null) {
				FSUtilLogger.error("table not exist:" + tableName);
				return null;
			}
			wrap = new TableUpdateContainer<Object, Object>(tableName, sql, this.periodMillis);
			TableUpdateContainer<Object, Object> old = containerMap.putIfAbsent(tableName, wrap);
			if (old != null) {
				wrap = old;
			}
		}
		return wrap;
	}

	public synchronized void addTableSqlMapper(Map<String, String> map) {
		HashMap<String, String> tableSqlCopy = new HashMap<String, String>(this.tableSqlMapper);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String sql = entry.getValue();
			if (key == null || sql == null) {
				throw new ExceptionInInitializerError("key = " + key + ",sql = " + sql);
			}
			tableSqlCopy.put(key, sql);
		}
		this.tableSqlMapper = tableSqlCopy;
	}

	@Override
	public void run() {
		for (Map.Entry<String, TableUpdateContainer<Object, Object>> entry : containerMap.entrySet()) {
			TableUpdateContainer<Object, Object> wrap = entry.getValue();
			if (wrap.acquireRunSignal()) {
				taskExecutor.asyncExecute(entry.getKey(), wrap);
			} else {
				// TODO Logger
			}
		}
	}

	public void addEvictedTask(String tableName, EvictedElementTaker taker) {
		TableUpdateContainer<Object, Object> wrap = getTableUpdateWrap(tableName);
		if (wrap == null) {
			FSUtilLogger.error("evicted fail cause by table not exist:" + tableName);
			return;
		}
		if (wrap.addEvictedTask(taker)) {
			taskExecutor.asyncExecute(tableName, wrap);
		}
	}

	public EvictedUpdateTask<Object> getEvictedTask(String tableName) {
		TableUpdateContainer<Object, Object> wrap = getTableUpdateWrap(tableName);
		if (wrap == null) {
			return null;
		}
		return wrap.getEvictTask();
	}

	public Enumeration<TableUpdateContainer<Object, Object>> getAllContainer() {
		return containerMap.elements();
	}
}
