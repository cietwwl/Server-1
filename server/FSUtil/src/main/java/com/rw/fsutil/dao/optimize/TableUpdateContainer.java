package com.rw.fsutil.dao.optimize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.jdbc.core.JdbcTemplate;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.concurrent.ParametricTask;
import com.rw.fsutil.dao.cache.CacheLogger;
import com.rw.fsutil.dao.cache.evict.EvictedUpdateTask;
import com.rw.fsutil.util.DateUtils;

public class TableUpdateContainer<K, K2> implements ParametricTask<Void>, EvictedUpdateTask<K2> {

	private String tableName;
	private ConcurrentLinkedQueue<EvictedElementTaker> evictedQueue;
	private ConcurrentSkipListMap<QueueObject, Object> updateQueue;
	private String sql;
	private final int updateCount;
	private final JdbcTemplate template;
	private final UpdateCountStat updateStat;
	private final UpdateCountStat evictStat;
	private boolean running;
	private final long checkRunPeriodMillis;
	private final AtomicLong seqGenerator;
	private static final Boolean PRESENT = Boolean.TRUE;

	public TableUpdateContainer(String tableName, String sql, long checkRunPeriodMillis) {
		this.tableName = tableName;
		this.updateQueue = new ConcurrentSkipListMap<QueueObject, Object>();
		this.evictedQueue = new ConcurrentLinkedQueue<EvictedElementTaker>();
		this.template = DataAccessFactory.getSimpleSupport().getMainTemplate();
		this.updateStat = new UpdateCountStat(tableName, "update");
		this.evictStat = new UpdateCountStat(tableName, "evict");
		this.updateCount = 10;
		this.sql = sql;
		this.checkRunPeriodMillis = checkRunPeriodMillis;
		this.seqGenerator = new AtomicLong();
	}

	public void addChanged(K2 key, long period, PersistentParamsExtractor<K2> extractor) {
		updateQueue.put(new QueueObject(key, DateUtils.getSecondLevelMillis() + period,extractor), PRESENT);
	}

	public boolean addEvictedTask(EvictedElementTaker evictedElementTaker) {
		this.evictedQueue.offer(evictedElementTaker);
		return acquireRunSignal();
	}

	public long updateForEvict(Map<K2, Object[]> paramsMap) {
		int size = paramsMap.size();
		ArrayList<Object[]> paramsList = new ArrayList<Object[]>(size);
		for (Map.Entry<K2, Object[]> entry : paramsMap.entrySet()) {
			paramsList.add(entry.getValue());
		}
		return updateToDB(System.currentTimeMillis(), paramsList, evictStat);
	}

	@Override
	public void updateForEvict(K2 key, Object[] param) {
		long start = System.currentTimeMillis();
		int result = template.update(sql, param);
		if (result == 1) {
			evictStat.incSuccess(System.currentTimeMillis() - start, 1);
		} else {
			recordException(evictStat, param, result);
		}
	}

	private long updateToDB(long startTime, List<Object[]> paramsList, UpdateCountStat stat) {
		int count = 0;
		//FSUtilLogger.info("执行同步：" + tableName + "," + paramsList);
		int[] result = template.batchUpdate(sql, paramsList);
		for (int i = result.length; --i >= 0;) {
			int rows = result[i];
			if (rows == 1) {
				count++;
				//FSUtilLogger.info("执行同步成功：" + tableName);
			} else {
				recordException(stat, paramsList.get(i), rows);
				//FSUtilLogger.info("执行同步失败：" + tableName);
			}
		}
		long end = System.currentTimeMillis();
		if (count > 0) {
			stat.incSuccess(end - startTime, count);
		}
		return end;
	}

	private void recordException(UpdateCountStat stat, Object[] param, int affectRow) {
		// TODO SQL可以整合，不用打印
		StringBuilder sb = new StringBuilder();
		sb.append("update fail by ");
		sb.append(stat.name).append(" affect=").append(affectRow).append(':').append(sql).append(CacheLogger.lineSeparator);
		sb.append(Arrays.toString(param));
		FSUtilLogger.error(sb.toString());
		if (affectRow == 0) {
			stat.incFail();
		} else {
			stat.incException();
		}
	}

	public boolean acquireRunSignal() {
		synchronized (this) {
			if (this.running) {
				return false;
			} else {
				running = true;
				return true;
			}
		}
	}

	@Override
	public void run(Void v) {
		try {
			long initTimeMillis = System.currentTimeMillis();
			// long nextStartTime = initTimeMillis;
			for (;;) {
				EvictedElementTaker taker = this.evictedQueue.poll();
				if (taker == null) {
					break;
				}
				runEvictedTask(taker);
			}
			// 执行update任务，最少执行一次(如果有)，会检查时间伐值
			ArrayList<Object[]> paramsList = new ArrayList<Object[]>();
			long nextStartTime = System.currentTimeMillis();
			for (;;) {
				Map.Entry<QueueObject, Object> firstEntry = updateQueue.firstEntry();
				if (firstEntry == null) {
					break;
				}
				QueueObject queueObject = firstEntry.getKey();
				if ((initTimeMillis < queueObject.executeTimeMillis)) {
					break;
				}
				firstEntry = updateQueue.pollFirstEntry();
				if (firstEntry == null) {
					break;
				}
				queueObject = firstEntry.getKey();
				K2 key = queueObject.key;
				if (!queueObject.extractor.extractParams(key, paramsList)) {
					FSUtilLogger.error("extract params fail:" + key + "," + tableName);
					continue;
				}
				if (paramsList.size() >= updateCount) {
					nextStartTime = updateToDB(nextStartTime, paramsList, updateStat);
					if ((nextStartTime - initTimeMillis) > checkRunPeriodMillis) {
						break;
					}
					paramsList.clear();
				}
			}
			if (!paramsList.isEmpty()) {
				updateToDB(nextStartTime, paramsList, updateStat);
			}
		} catch (Throwable e) {
			FSUtilLogger.error("update exception:" + tableName + "," + sql, e);
		}
		// 再次检查执行evictedQueue任务，如果没有evictedQueue任务，退出循环
		for (;;) {
			EvictedElementTaker taker;
			synchronized (this) {
				try {
					taker = this.evictedQueue.poll();
				} catch (Throwable t) {
					this.running = false;
					FSUtilLogger.error("raise an exception by polling task,tableName=" + tableName, t);
					return;
				}
				if (taker == null) {
					this.running = false;
					return;
				}
			}
			runEvictedTask(taker);
		}
	}

	private void runEvictedTask(EvictedElementTaker taker) {
		try {
			taker.takeTask().run();
		} catch (Throwable t) {
			FSUtilLogger.error("raise an exception cause by running task,tableName=" + tableName, t);
		}
	}

	public EvictedUpdateTask<K2> getEvictTask() {
		return this;
	}

	public UpdateCountStat getEvictStat() {
		return evictStat;
	}

	public UpdateCountStat getUpdateStat() {
		return updateStat;
	}

	public String getTableName() {
		return tableName;
	}

	class QueueObject implements Comparable<QueueObject> {

		final K2 key;
		final long executeTimeMillis;
		final long seqId;
		final PersistentParamsExtractor<K2> extractor;

		public QueueObject(K2 key, long executeTimeMillis,PersistentParamsExtractor<K2> extractor) {
			super();
			this.key = key;
			this.executeTimeMillis = executeTimeMillis;
			this.seqId = seqGenerator.incrementAndGet();
			this.extractor = extractor;
		}

		@Override
		public int compareTo(QueueObject o) {
			long otherTimeMillis = o.executeTimeMillis;
			if (executeTimeMillis < otherTimeMillis) {
				return -1;
			}
			if (executeTimeMillis > otherTimeMillis) {
				return 1;
			}
			if (seqId < o.seqId) {
				return -1;
			} else if (seqId > o.seqId) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}
