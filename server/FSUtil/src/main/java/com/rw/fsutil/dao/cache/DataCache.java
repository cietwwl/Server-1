package com.rw.fsutil.dao.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.dao.cache.evict.EldestDefaultHandler;
import com.rw.fsutil.dao.cache.evict.EldestEvictedResult;
import com.rw.fsutil.dao.cache.evict.EldestHandler;
import com.rw.fsutil.dao.cache.evict.EvictedUpdateTask;
import com.rw.fsutil.dao.cache.record.DataLoggerRecord;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataChangedEvent;
import com.rw.fsutil.dao.cache.trace.DataChangedVisitor;
import com.rw.fsutil.dao.optimize.ComputeFunction;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataValueAction;
import com.rw.fsutil.dao.optimize.EvictedElementTaker;
import com.rw.fsutil.dao.optimize.FSBoundedQueue;
import com.rw.fsutil.dao.optimize.FSLRUCache;
import com.rw.fsutil.dao.optimize.LRUInsertResult;
import com.rw.fsutil.dao.optimize.PersistentGenericHandler;
import com.rw.fsutil.dao.optimize.TableUpdateCollector;
import com.rw.fsutil.dao.optimize.ValueConsumer;
import com.rw.fsutil.util.DateUtils;

/**
 * 数据缓存
 * 
 * @author Jamaz
 */
@SuppressWarnings("rawtypes")
public abstract class DataCache<K, V> implements EvictedElementTaker {

	protected final PersistentGenericHandler<K, V, Object> loader;
	protected final FSLRUCache<K, CacheValueEntity<V>> cache;
	protected final int updatePeriodMillis;
	protected final CacheLogger logger;
	protected String name;
	private final ConcurrentHashMap<K, ReentrantFutureTask> taskMap; // 任务集合，表示一个以主键互斥的任务
	private final ConcurrentHashMap<K, Object> recordMap; // 表示正在执行record的对象
	private final SimpleCache<K, Long> penetrationCache;// 缓存穿透，表示在数据库找不到的任务，缓存一段时间防止缓存穿透
	private final long penetrationTimeOutMillis;
	private final int capacity;
	private final int updatePeriod;
	private final long timeoutMillis;
	private final DataNotExistHandler<K, V> dataNotExistHandler;
	private CacheJsonConverter<K, V, Object, DataChangedEvent<?>> jsonConverter;
	private final ArrayList<DataChangedVisitor<DataChangedEvent<?>>> dataChangedListeners;
	private ComputeFunction<CacheValueEntity<V>, V> computeFunction;
	private ValueConsumer<CacheValueEntity<V>, V, Pair<V, CacheValueEntity<V>>> updateComsumer;
	private final FSBoundedQueue<ReentrantFutureTask> evictedQueue;

	public DataCache(CacheKey key, int maxCapacity, int updatePeriod, PersistentGenericHandler<K, V, ? extends Object> loader, DataNotExistHandler<K, V> dataNotExistHandler,
			CacheJsonConverter<K, V, ?, ? extends DataChangedEvent<?>> jsonConverter, List<DataChangedVisitor<DataChangedEvent<?>>> dataChangedListeners) {
		this.name = key.getName();
		int tenPercent = maxCapacity / 10;
		this.capacity = maxCapacity - tenPercent;
		this.evictedQueue = new FSBoundedQueue<ReentrantFutureTask>(name, tenPercent);
		this.logger = CacheFactory.getLogger(name);
		this.updatePeriod = updatePeriod;
		this.updatePeriodMillis = (int) TimeUnit.SECONDS.toMillis(updatePeriod);
		this.taskMap = new ConcurrentHashMap<K, ReentrantFutureTask>();
		this.penetrationCache = new SimpleCache<K, Long>(1000);
		this.penetrationTimeOutMillis = TimeUnit.MINUTES.toMillis(5);
		this.loader = (PersistentGenericHandler<K, V, Object>) loader;
		this.timeoutMillis = TimeUnit.SECONDS.toMillis(10);
		if (dataNotExistHandler != null) {
			this.dataNotExistHandler = dataNotExistHandler;
		} else {
			this.dataNotExistHandler = new DefualtDataNotExistHandler();
		}
		this.jsonConverter = (CacheJsonConverter<K, V, Object, DataChangedEvent<?>>) jsonConverter;
		if (this.jsonConverter != null) {
			recordMap = new ConcurrentHashMap<K, Object>();
		} else {
			recordMap = null;
		}
		if (dataChangedListeners != null) {
			this.dataChangedListeners = new ArrayList<DataChangedVisitor<DataChangedEvent<?>>>(dataChangedListeners);
		} else {
			this.dataChangedListeners = new ArrayList<DataChangedVisitor<DataChangedEvent<?>>>(0);
		}

		EldestHandler<K, CacheValueEntity<V>> eldestHandler;
		Map<String, String> updateSqlMapping = loader.getUpdateSqlMapping();
		if (updateSqlMapping != null) {
			DataAccessFactory.getTableUpdateCollector().addTableSqlMapper(updateSqlMapping);
			eldestHandler = new EldestHandlerImpl();
		} else {
			eldestHandler = new EldestDefaultHandler<K, V>();
		}

		this.computeFunction = new ComputeFunction<CacheValueEntity<V>, V>() {

			@Override
			public V computeIfPersent(CacheValueEntity<V> value) {
				return value.getValue();
			}
		};

		this.updateComsumer = new ValueConsumer<CacheValueEntity<V>, V, Pair<V, CacheValueEntity<V>>>() {

			@Override
			public Pair<V, CacheValueEntity<V>> apply(CacheValueEntity<V> value, V newValue) {
				V old = value.getValue();
				if (old == newValue) {
					return Pair.Create(null, value);
				}
				value.setValue(newValue);
				return Pair.Create(old, value);
			}
		};

		this.cache = new FSLRUCache<K, CacheValueEntity<V>>(name, maxCapacity, eldestHandler);
		this.logger.info("create DAO = " + name + ",maxCapacity = " + maxCapacity + ",updatePeriod = " + updatePeriod);
	}

	class DefualtDataNotExistHandler implements DataNotExistHandler<K, V> {

		@Override
		public V callInLoadTask(K key) {
			// 缓存穿透处理
			logger.info("load not exist data:" + key);
			penetrationCache.put(key, DateUtils.getSecondLevelMillis());
			return null;
		}

	}

	private static class EldestEvictedResultImpl implements EldestEvictedResult {

		private final boolean readyToEvicted;
		private final String name;

		public EldestEvictedResultImpl() {
			this.readyToEvicted = true;
			this.name = null;
		}

		public EldestEvictedResultImpl(String name) {
			this.readyToEvicted = false;
			this.name = name;
		}

		@Override
		public boolean readyToEvicted() {
			return readyToEvicted;
		}

		@Override
		public String getBlockingName() {
			return name;
		}

	}

	private class EldestHandlerImpl implements EldestHandler<K, CacheValueEntity<V>> {

		private EldestEvictedResultImpl successResult = new EldestEvictedResultImpl();

		@Override
		public EldestEvictedResult beforeElementEvicted(Entry<K, CacheValueEntity<V>> eldest) {
			K eldestKey = eldest.getKey();
			CacheValueEntity<V> value = eldest.getValue();
			String tableName = value.getTableName();
			TableUpdateCollector updateCollector = DataAccessFactory.getTableUpdateCollector();
			EvictedUpdateTask<Object> updateTask = updateCollector.getEvictedTask(tableName);
			if (updateTask == null) {
				FSUtilLogger.error("evicted element directly:" + tableName);
				return successResult;
			}
			if (!loader.hasChanged(eldestKey, value.getValue())) {
				return successResult;
			}
			ReentrantFutureTask evictedTask = createEvictedTask(new EvictedTask(eldestKey, value, updateTask));
			ReentrantFutureTask old = taskMap.putIfAbsent(eldestKey, evictedTask);
			if (old != null) {
				logger.error(name + " fatal@save evicted task fail:" + eldestKey + ',' + old + ',' + getThreadAndTime());
			} else {
				// here maybe be relive
			}

			boolean addTask = evictedQueue.offer(evictedTask);
			if (addTask) {
				updateCollector.addEvictedTask(tableName, DataCache.this);
				return successResult;
			} else {
				if (!taskMap.remove(eldestKey, evictedTask)) {
					FSUtilLogger.info(name + " relive element：" + eldestKey);
				}
				evictedTask.cancel(false);
				return new EldestEvictedResultImpl(tableName);
			}
		}

	}

	/** 插入任务 **/
	class InsertTask extends TaskCallable<CacheValueEntity<V>> {

		private final V value;
		private final boolean insertDB;
		private final CacheStackTrace trace;

		public InsertTask(K key, V value, boolean insertDB, CacheStackTrace trace) {
			super(key);
			this.value = value;
			this.insertDB = insertDB;
			this.trace = trace;
		}

		@Override
		public CacheValueEntity<V> call() throws Exception {
			boolean duplicatedKey = false;
			if (insertDB) {
				try {
					loader.insert(key, value);
				} catch (DuplicatedKeyException ex) {
					logger.error("insert db DuplicatedKey:" + key + "," + value, ex);
					duplicatedKey = true;
				}
			}

			Object record = copy(key, value);
			CacheValueEntity<V> cacheValue = new CacheValueEntity<V>(value, record, loader.getTableName(key));
			CacheValueEntity<V> oldValue = putIfAbsent(key, cacheValue);
			// 与put方法有冲突
			if (oldValue != null) {
				logger.error("put into cache exist data:" + key + "," + duplicatedKey);
				// 更新缓存的值
				oldValue.setValue(value);
				record(key, value, oldValue, trace);
				// DataCache.this.submitUpdateTask(key);
				notifyValueUpdate(key, oldValue, true);
			} else if (record != null) {
				DataLoggerRecord event = parse(key, record);
				if (event != null) {
					logger.executeAysnEvent(CacheLoggerPriority.INFO, "I", event, trace);
				}
			}
			// 缓存有旧值
			if (oldValue != null || duplicatedKey) {
				logger.error("insert fail:" + insertDB + "," + key + ",exist=" + duplicatedKey + "," + oldValue + "," + value);
			}
			return oldValue;
		}

		@Override
		public String getName() {
			return "insertTask";
		}
	}

	class EvictedTask extends TaskCallable<Void> {

		private volatile CacheValueEntity<V> value;
		private final EvictedUpdateTask<Object> updateTask;

		// private final String threadName;

		public EvictedTask(K key, CacheValueEntity<V> value, EvictedUpdateTask<Object> updateTask) {
			super(key);
			// this.threadName = Thread.currentThread().getName();
			this.value = value;
			this.updateTask = updateTask;
		}

		@Override
		public Void call() throws Exception {
			CacheValueEntity<V> value = this.value;
			if (value == null) {
				FSUtilLogger.info(name + " has been relive：" + key);
				return null;
			}
			HashMap<Object, Object[]> entityMap = new HashMap<Object, Object[]>();
			loader.extractParams(key, value.getValue(), entityMap);
			updateTask.updateForEvict(entityMap);
			return null;
		}

		@Override
		public String getName() {
			return "evicted";
		}

		public CacheValueEntity<V> getValue() {
			return value;
		}

		public void clear() {
			this.value = null;
		}
	}

	class InsertRelive extends TaskCallable<CacheValueEntity<V>> {

		private final CacheValueEntity<V> cacheValue;

		public InsertRelive(K key, CacheValueEntity<V> value) {
			super(key);
			this.cacheValue = value;
		}

		@Override
		public CacheValueEntity<V> call() throws Exception {
			CacheValueEntity<V> oldValue = putIfAbsent(key, cacheValue);
			if (oldValue != null) {
				logger.error("relive insert cache exist data:" + key);
			} else {
				logger.warn("relive insert cache:" + key);
			}
			return oldValue;
		}

		@Override
		public String getName() {
			return "insertTask";
		}

	}

	class LoadRelive extends TaskCallable<CacheValueEntity<V>> {

		private final CacheValueEntity<V> value;

		public LoadRelive(K key, CacheValueEntity<V> value) {
			super(key);
			this.value = value;
		}

		@Override
		public CacheValueEntity<V> call() throws Exception {
			CacheValueEntity<V> old = putIfAbsent(key, value);
			if (old != null) {
				return old;
			} else {
				return value;
			}
		}

		@Override
		public String getName() {
			return "loadRelive";
		}

	}

	/**
	 * 获取指数据，如果不存在，尝试从数据库中加载该主键对应的数据
	 * 
	 * @param key 数据的主键
	 * @return 指定的数据
	 * @throws InterruptedException 在加载的过程中可能抛出{@link InterruptedException}
	 *             ，但此时不能确定数据是否已经加载完成
	 * @throws Throwable 在加载的过程中可能抛出其他的异常，需要自己进行捕捉
	 */
	public V getOrLoadFromDB(K key) throws InterruptedException, Throwable {
		// 先从缓存中获取，如果存在于缓存，直接返回
		CacheValueEntity<V> cacheValue = getOrLoadCacheFromDB(key, false);
		return cacheValue == null ? null : cacheValue.getValue();
	}

	/**
	 * <pre>
	 * 获取指数据，如果不存在，尝试从数据库中加载该主键对应的数据
	 * 如果readOnly为true，不会影响对象本身的内存管理(不会重排序)，且获得更好的并发性
	 * </pre>
	 * @param key
	 * @param readOnly
	 * @return
	 * @throws InterruptedException
	 * @throws Throwable
	 */
	public V getOrLoadFromDB(K key, boolean readOnly) throws InterruptedException, Throwable {
		// 先从缓存中获取，如果存在于缓存，直接返回
		CacheValueEntity<V> cacheValue = getOrLoadCacheFromDB(key, readOnly);
		return cacheValue == null ? null : cacheValue.getValue();
	}

	public CacheValueEntity<V> getOrLoadCacheFromDB(K key, boolean readOnly) throws InterruptedException, Throwable {
		// 先从缓存中获取，如果存在于缓存，直接返回
		CacheValueEntity<V> value;
		if (readOnly) {
			value = this.cache.getWithOutMove(key);
		} else {
			value = this.cache.get(key);
		}
		this.cache.get(key);
		if (value != null) {
			return value;
		}
		// 命中缓存穿透
		// TODO 这里可用LRU代替
		if (penetrationCache != null) {
			Long time = this.penetrationCache.get(key);
			if (time != null) {
				if (DateUtils.getSecondLevelMillis() - time < penetrationTimeOutMillis) {
					return null;
				} else {
					this.penetrationCache.remove(key);
				}
			}
		}
		// 从数据库加载
		return loadFromDB(key, new CacheStackTrace());
	}

	private CacheValueEntity<V> loadFromDB(K key, CacheStackTrace trace) throws InterruptedException, TimeoutException, Throwable {
		// 创建加载任务
		ReentrantFutureTask<CacheValueEntity<V>> task = createTask(new LoadTask(key, trace));
		long lastTime = System.currentTimeMillis();
		long remainMillis = timeoutMillis;
		for (;;) {
			FutureTask oldTask = submitKeyTask(key, task, OperationType.LOAD);
			if (oldTask == null) {
				task.run();
				try {
					return task.get(remainMillis, TimeUnit.NANOSECONDS);
				} catch (TimeoutException ex) {
					logger.error("loadFromDB timeout:" + TimeUnit.NANOSECONDS.toMillis(remainMillis) + "," + task, ex);
					throw ex;
				} catch (ExecutionException ex) {
					throw ex.getCause();
				}
			}
			long now = System.currentTimeMillis();
			remainMillis -= (now - lastTime);
			lastTime = now;
			Object result = null;
			try {
				result = oldTask.get(remainMillis, TimeUnit.MILLISECONDS);
			} catch (TimeoutException ex) {
				logger.error("wait for other task loadFromDB timeout:" + TimeUnit.NANOSECONDS.toMillis(remainMillis) + "," + oldTask, ex);
				throw ex;
			} catch (ExecutionException ex) {
				// ignore
			}
			now = System.currentTimeMillis();
			remainMillis -= (now - lastTime);
			lastTime = now;
			if (result == null || result instanceof Boolean) {
				continue;
			}
			CacheValueEntity<V> value = this.cache.get(key);
			if (value != null) {
				return value;
			}
		}
	}

	/**
	 * 删除指定数据，在一定时间后同步到DB
	 * 
	 * @param key
	 * @return
	 * @throws InterruptedException
	 * @throws Throwable
	 */
	public boolean removeAndUpdateToDB(K key) throws InterruptedException, Throwable {
		ReentrantFutureTask<Boolean> removeTask = submitAndRun(key, new RemoveTask(key), OperationType.REMOVE);
		return removeTask.get();
	}

	/**
	 * 从缓存中获取指定数据
	 * 
	 * @param key
	 * @return
	 */
	public V getFromMemory(K key) {
		CacheValueEntity<V> entity = this.cache.getWithOutMove(key);
		return entity == null ? null : entity.getValue();
	}

	/**
	 * 从缓存中获取指定数据用于读行为
	 * 
	 * @param key
	 * @return
	 */
	public V getFromMemoryForRead(K key) {
		CacheValueEntity<V> entity = this.cache.getWithOutMove(key);
		return entity == null ? null : entity.getValue();
	}

	/**
	 * 从缓存中获取指定数据用于写行为
	 * 
	 * @param key
	 * @return
	 */
	public V getFromMemoryForWrite(K key) {
		CacheValueEntity<V> entity = this.cache.get(key);
		return entity == null ? null : entity.getValue();
	}

	private CacheValueEntity<V> update(final K key, final V value) throws DataDeletedException {
		Pair<V, CacheValueEntity<V>> result = this.cache.computeIfPresent(key, this.updateComsumer, value);
		if (result == null) {
			return null;
		}
		V oldValue = result.getT1();
		CacheStackTrace trace;
		boolean replace;
		if (oldValue != null) {
			trace = new CacheStackTrace("replace value:" + key);
			trace.printStackTrace();
			replace = true;
		} else {
			trace = new CacheStackTrace();
			replace = false;
		}

		CacheValueEntity<V> old = result.getT2();
		// 此Json可能是一个中间值
		record(key, value, old, trace);
		notifyValueUpdate(key, old, replace);
		return old;
	}

	protected void record(K key, V value, CacheValueEntity<V> entity, CacheStackTrace trace) {
		try {
			if (!CacheLoggerSwitch.getInstance().isCacheLogger(name)) {
				return;
			}
			CacheJsonConverter<K, V, Object, DataChangedEvent<?>> converter = DataCache.this.jsonConverter;
			if (converter == null) {
				return;
			}
			Object record = entity.getRecord();
			if (record == null) {
				return;
			}
			V current = entity.getValue();
			if (recordMap.putIfAbsent(key, entity) != null) {
				String error = "multi thread record:" + key + "," + name;
				logger.fatal(error);
				new RuntimeException(error).printStackTrace();
				synchronized (entity) {
					if (recordMap.putIfAbsent(key, entity) != null) {
						logger.fatal("retry to record fail:" + key + "," + name);
						return;
					}
				}
			}
			synchronized (entity) {
				try {
					DataChangedEvent<?> event = converter.produceChangedEvent(key, entity.getRecord(), value);
					for (int i = this.dataChangedListeners.size(); --i >= 0;) {
						DataChangedVisitor<DataChangedEvent<?>> listener = dataChangedListeners.get(i);
						try {
							listener.notifyDataChanged(event);
						} catch (Exception t) {
							logger.error("notify data changed exception:" + key, t);
						}
					}
					DataLoggerRecord recordEvent = converter.parseAndUpdate(key, record, current, event);
					logger.executeAysnEvent(CacheLoggerPriority.INFO, "U", recordEvent, trace);
				} finally {
					recordMap.remove(key);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private Object copy(K key, V value) {
		try {
			if (!CacheLoggerSwitch.getInstance().isCacheLogger(name)) {
				return null;
			}
			CacheJsonConverter<K, V, ?, ?> converter = DataCache.this.jsonConverter;
			if (converter == null) {
				return null;
			}
			return converter.copy(key, value);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	private DataLoggerRecord parse(K key, Object value) {
		try {
			if (!CacheLoggerSwitch.getInstance().isCacheLogger(name)) {
				return null;
			}
			CacheJsonConverter<K, V, Object, ?> converter = DataCache.this.jsonConverter;
			if (converter == null) {
				return null;
			}
			return converter.parse(key, value);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	/**
	 * <pre>
	 * 添加一个数据到缓存中
	 * 尝试修改内存，若不存在会尝试插入到数据库
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws DataDeletedException
	 * @throws InterruptedException
	 */
	public V put(K key, V value) throws DataDeletedException, InterruptedException, Throwable {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		// 1.尝试直接直接修改内存
		// 2.如果不在内存，起一个insert任务
		CacheValueEntity<V> old = putAndGet(key, value);
		return old == null ? null : old.getValue();
	}

	/**
	 * <pre>
	 * 当缓存中不存在此键值对时预插入
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean preInsertIfAbsent(K key, V value) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		if (this.cache.containsKey(key)) {
			return false;
		}
		return safeInsertCache(key, value, new CacheStackTrace());
	}

	public boolean preInsertIfAbsent(K key, Callable<V> valueExtractor) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		if (valueExtractor == null) {
			throw new NullPointerException("valueExtractor is null");
		}
		if (this.cache.containsKey(key)) {
			return false;
		}
		try {
			V value = valueExtractor.call();
			if (value != null) {
				return safeInsertCache(key, value, new CacheStackTrace());
			} else {
				logger.error("extract value is null:" + key);
				return false;
			}
		} catch (Exception e) {
			logger.error("extract value exception:" + key, e);
			return false;
		}

	}

	private boolean safeInsertCache(K key, V value, CacheStackTrace trace) {
		try {
			ReentrantFutureTask task = submitAndRun(key, new InsertTask(key, value, false, trace), OperationType.INSERT);
			if (task.isRelive()) {
				return false;
			} else {
				return task.get() == null;
			}
		} catch (InterruptedException e) {
			logger.error("insert cache fail by interrupted:" + key + "," + value);
		} catch (TimeoutException e) {
			logger.error("insert cache fail by timeout:" + key + "," + value);
		} catch (ExecutionException e) {
			logger.error("insert cache exception:" + key + "," + value, e);
		}
		return false;
	}

	private CacheValueEntity<V> putAndGet(K key, V value) throws DataDeletedException, InterruptedException, Throwable {
		// 1.尝试直接直接修改内存
		// 2.如果不在内存，起一个insert任务
		ReentrantFutureTask<CacheValueEntity<V>> insertTask = null;
		for (;;) {
			// 尝试直接修改缓存数据
			CacheValueEntity<V> result = update(key, value);
			if (result != null) {
				// 修改内存值成功
				return result;
			}
			if (insertTask == null) {
				insertTask = createTask(new InsertTask(key, value, true, new CacheStackTrace()));
			}
			// 数据不存缓存，起一个插入数据库任务
			// 为了适应逻辑，主键重复会直接捕捉和记log，并直接执行插入工作
			FutureTask old = submitKeyTask(key, insertTask, OperationType.INSERT);
			if (old == null) {
				// 获取执行权后执行插入任务
				insertTask.run();
				try {
					return insertTask.get();
				} catch (ExecutionException ex) {
					throw ex.getCause();
				}
			}
		}
	}

	private FutureTask submitKeyTask(K key, ReentrantFutureTask task, OperationType opType) throws InterruptedException, TimeoutException {
		ReentrantFutureTask otherTask = this.taskMap.get(key);
		if (otherTask == null) {
			otherTask = this.taskMap.putIfAbsent(key, task);
			// 获取了任务的执行权
			if (otherTask == null) {
				// logger.info("add task:" + key + "," + task);
				return null;
			}
		}

		// 如果该任务是由当前线程执行，直接返回
		EvictedTask evictedTask = otherTask.getEvictedTask();
		if (evictedTask == null) {
			if (((ReentrantFutureTask) otherTask).getRunner() == Thread.currentThread()) {
				logger.warn("run in same thread:" + otherTask + "," + task);
				task.setUnderControl();
				return null;
			}
		} else {
			CacheValueEntity<V> entity = evictedTask.getValue();
			if (entity == null) {
				FSUtilLogger.error("relive value is null:" + key + "," + name);
			} else {
				ReentrantFutureTask newTask = opType.create(this, key, entity);
				if (taskMap.replace(key, otherTask, newTask)) {
					evictedTask.clear();
					newTask.run();
					return newTask;
				}
			}
		}
		// 这里执行其他操作可能会加载到内存
		try {
			otherTask.get(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (ExecutionException ex) {
			// ignore
		} catch (TimeoutException e) {
			logger.error("wait task timeout:" + otherTask, e);
			throw e;
		}
		return otherTask;
	}

	private CacheValueEntity<V> putIfAbsent(K key, CacheValueEntity<V> entity) throws InterruptedException, TimeoutException {
		long remainMillis = this.timeoutMillis;
		for (;;) {
			LRUInsertResult<CacheValueEntity<V>> result = this.cache.putIfAbsent(key, entity);
			if (result == null) {
				return null;
			}
			CacheValueEntity<V> old = result.getValue();
			if (old != null) {
				return old;
			}
			// long start = System.currentTimeMillis();
			boolean ok = this.evictedQueue.waitForNotFull(TimeUnit.MILLISECONDS, remainMillis);
			if (!ok) {
				throw new TimeoutException();
			}
			// remainMillis -= (System.currentTimeMillis() - start);
		}
	}

	/**
	 * 检测是否含有该键对应的数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key) {
		return this.cache.containsKey(key);
	}

	private <T> ReentrantFutureTask<T> submitAndRun(K key, TaskCallable<T> task, OperationType opType) throws InterruptedException, TimeoutException {
		// 1.产生一个正式的Task
		// 2.替换成功，执行
		// 3.替换失败，判断如果是踢出任务，产生一个relive task，执行
		// 4.替换失败，不是踢出任务，等待此任务执行完，循环
		ReentrantFutureTask t = new ReentrantFutureTask(task);
		for (;;) {
			FutureTask old = submitKeyTask(key, t, opType);
			// 插入成功
			if (old == null) {
				t.run();
				return t;
			} else if (old instanceof ReentrantFutureTask) {
				ReentrantFutureTask oldTask = (ReentrantFutureTask) old;
				if (oldTask.isRelive()) {
					return oldTask;
				}
			}
		}
	}

	/** 构造一个ReentrantFutureTask **/
	private ReentrantFutureTask createTask(TaskCallable task) {
		ReentrantFutureTask t = new ReentrantFutureTask(task);
		return t;
	}

	/** 构造一个ReentrantFutureTask **/
	private <T> ReentrantFutureTask<T> createReliveTask(TaskCallable<T> task) {
		ReentrantFutureTask t = new ReentrantFutureTask(task, true);
		return t;
	}

	/** 构造一个ReentrantFutureTask **/
	private ReentrantFutureTask createEvictedTask(EvictedTask task) {
		ReentrantFutureTask t = new ReentrantFutureTask(task);
		return t;
	}

	/** 加载任务 **/
	class LoadTask extends TaskCallable<CacheValueEntity<V>> {

		private final CacheStackTrace trace;

		public LoadTask(K key, CacheStackTrace trace) {
			super(key);
			this.trace = trace;
		}

		@Override
		public CacheValueEntity<V> call() throws Exception {
			V v = null;
			try {
				// 从db加载数据
				v = loader.load(key);
			} catch (DataNotExistException ex) {
				v = dataNotExistHandler.callInLoadTask(key);
			}
			if (v != null) {
				Object record = copy(key, v);
				CacheValueEntity<V> cacheValue = new CacheValueEntity<V>(v, record, loader.getTableName(key));
				CacheValueEntity<V> old = putIfAbsent(key, cacheValue);
				if (old != null) {
					return old;
				}
				if (record != null) {
					DataLoggerRecord event = parse(key, record);
					if (event != null) {
						logger.executeAysnEvent(CacheLoggerPriority.INFO, "L", event, this.trace);
					}
				}
				return cacheValue;
			} else {
				return null;
			}
		}

		@Override
		public String getName() {
			return "loadTask:" + key;
		}
	}

	/** 移除任务 **/
	class RemoveTask extends TaskCallable<Boolean> {

		public RemoveTask(K key) {
			super(key);
		}

		@Override
		public Boolean call() throws Exception {
			CacheValueEntity<V> entity = cache.remove(key);
			boolean inCache = entity != null;
			try {
				boolean result = loader.delete(key);
				logger.info("delete task:" + key + ",db=" + result + ",cache=" + inCache);
				return result;
			} catch (DataNotExistException e) {
				logger.error("delete fail:" + key + "," + name + "," + inCache);
			} catch (Exception e) {
				logger.error("delete exception:" + key + "," + name + "," + inCache, e);
			}
			return Boolean.FALSE;
			// 这个时候其他线程无法加载从数据库加载的任务
			// CacheValueEntity<V> value = DataCache.this.get(key);
			// 如果不在内存，直接从数据库执行删除
			// 如果在内存，DELETED状态不处理，因为已被删除
			// 如果在内存，MARK_DELETE，从数据库执行删除，并且设置为DELETED
			// 如果在内存，PERSISTENT，重新设置为MARK_DELETE，从数据库执行删除，并且设置为DELETED
		}

		@Override
		public String getName() {
			return "removeTask";
		}
	}

	/**
	 * 获取缓存当前的大小
	 *
	 * @return
	 */
	public int size() {
		return this.cache.size();
	}

	/**
	 * 获取缓存的最大容量
	 * 
	 * @return
	 */
	public int getMaxCapacity() {
		return this.capacity;
	}

	/**
	 * 获取缓存的更新周期，单位秒
	 * 
	 * @return
	 */
	public int getUpdatePeriod() {
		return this.updatePeriod;
	}

	/**
	 * 获取缓存key-value的拷贝
	 *
	 * @return
	 */
	public Map<K, V> entries() {
		return cache.entries(computeFunction);
	}

	/**
	 * <pre>
	 * 对缓存数据进行批量读操作，数据不在内存会从数据库记载
	 * </pre>
	 * @param keys
	 * @param readerAction
	 */
	public void rangeRead(Iterable<K> keys, DataValueAction<V> readerAction) {
		HashSet<K> absentKeys = null;
		for (Iterator<K> it = keys.iterator(); it.hasNext();) {
			K key = it.next();
			V value = getFromMemoryForRead(key);
			if (value == null) {
				if (absentKeys == null) {
					absentKeys = new HashSet<K>();
				}
				absentKeys.add(key);
				continue;
			}
			try {
				readerAction.execute(value);
			} catch (Throwable t) {
				FSUtilLogger.error("raised an exception on rangeRead:" + key + "," + name, t);
			}
		}
		if (absentKeys != null) {
			doAction(absentKeys, readerAction);
		}
	}

	/**
	 * <pre>
	 * 对缓存数据进行批量写操作，数据不在内存会从数据库记载
	 * </pre>
	 * @param keys
	 * @param writeAction
	 */
	public void rangeWrite(Iterable<K> keys, DataValueAction<V> writeAction) {
		HashSet<K> absentKeys = null;
		for (Iterator<K> it = keys.iterator(); it.hasNext();) {
			K key = it.next();
			V value = getFromMemoryForWrite(key);
			if (value == null) {
				if (absentKeys == null) {
					absentKeys = new HashSet<K>();
				}
				absentKeys.add(key);
				continue;
			}
			try {
				writeAction.execute(value);
			} catch (Throwable t) {
				FSUtilLogger.error("raised an exception on rangeWrite:" + key + "," + name, t);
			}
		}
		if (absentKeys != null) {
			doAction(absentKeys, writeAction);
		}
	}

	private void doAction(HashSet<K> absentKeys, DataValueAction<V> writeAction) {
		for (K key : absentKeys) {
			try {
				V value = this.getOrLoadFromDB(key);
				if (value != null) {
					writeAction.execute(value);
				} else {
					FSUtilLogger.error("can not find value:" + key + "," + name);
				}
			} catch (Throwable t) {
				FSUtilLogger.error("raised an exception on get or execution:" + key + "," + name, t);
			}
		}
	}

	public PersistentGenericHandler<K, V, ? extends Object> getLoader() {
		return loader;
	}

	public Runnable createCacheUpdatedTask() {
		final Map<K, V> map = entries();
		return new Runnable() {

			@Override
			public void run() {
				for (Map.Entry<K, V> entry : map.entrySet()) {
					loader.updateToDB(entry.getKey(), entry.getValue());
				}
			}
		};
	}

	abstract class TaskCallable<T> implements Callable<T> {

		final K key;

		public TaskCallable(K key) {
			this.key = key;
		}

		public abstract String getName();
	}

	private class ReentrantFutureTask<T> extends FutureTask<T> {

		private final TaskCallable task;
		private volatile Thread runner;
		private volatile boolean controller;
		private final boolean relive;
		private final EvictedTask evictedTask;

		public ReentrantFutureTask(TaskCallable<T> task, boolean isRelive) {
			super(task);
			this.task = task;
			this.controller = true;
			this.relive = isRelive;
			this.evictedTask = null;
		}

		public ReentrantFutureTask(EvictedTask task) {
			super((TaskCallable) task);
			this.task = task;
			this.controller = true;
			this.relive = true;
			this.evictedTask = task;
		}

		public EvictedTask getEvictedTask() {
			return evictedTask;
		}

		public ReentrantFutureTask(TaskCallable<T> task) {
			this(task, false);
		}

		public boolean isRelive() {
			return relive;
		}

		public void setUnderControl() {
			this.controller = false;
		}

		@Override
		public void run() {
			try {
				if (runner == null) {
					this.runner = Thread.currentThread();
				}
				super.run();
			} finally {
				if (controller) {
					Future old = DataCache.this.taskMap.remove(task.key);
					if (old != this) {
						logger.error("fatal@" + task.getName());
					}
				}
			}
		}

		public Thread getRunner() {
			return this.runner;
		}

		public String toString() {
			return task.getName();
		}
	}

	public List<Runnable> createUpdateTask() {
		final Map<K, CacheValueEntity<V>> map = this.cache.entries();
		ArrayList<Runnable> taskList = new ArrayList<Runnable>(map.size());
		for (Map.Entry<K, CacheValueEntity<V>> entry : map.entrySet()) {
			final CacheValueEntity<V> cacheValue = entry.getValue();
			final K key = entry.getKey();
			taskList.add(new Runnable() {

				@Override
				public void run() {
					if (cacheValue == null) {
						logger.error("执行Future数据为null");
						return;
					}
					V value = cacheValue.getValue();
					try {
						logger.info("执行Future更新操作:" + key + "," + value);
						if (!loader.updateToDB(key, value)) {
							logger.error("执行Future更新失败:" + key);
						}
					} catch (Throwable t) {
						logger.error("执行Future异常:" + key + "," + value, t);
					}
				}
			});
		}
		return taskList;
	}

	public CacheLogger getLogger() {
		return this.logger;
	}

	public String getName() {
		return name;
	}

	// @Override
	public void submitRecordTask(K key) {
		CacheValueEntity<V> entity = this.cache.getWithOutMove(key);
		if (entity != null) {
			record(key, entity.getValue(), entity, new CacheStackTrace());
		}
	}

	static enum OperationType {
		INSERT {
			@Override
			public <K, V> DataCache.ReentrantFutureTask create(DataCache<K, V> cache, K key, CacheValueEntity<V> value) {
				return cache.createReliveTask(cache.new InsertRelive(key, value));
			}
		},
		REMOVE {
			@Override
			public <K, V> DataCache.ReentrantFutureTask create(DataCache<K, V> cache, K key, CacheValueEntity<V> value) {
				return cache.createReliveTask(cache.new RemoveTask(key));
			}
		},
		LOAD {
			@Override
			public <K, V> DataCache.ReentrantFutureTask create(DataCache<K, V> cache, K key, CacheValueEntity<V> value) {
				return cache.createReliveTask(cache.new LoadRelive(key, value));
			}
		};

		public abstract <K, V> DataCache.ReentrantFutureTask create(DataCache<K, V> cache, K key, CacheValueEntity<V> value);
	}

	@Override
	public Runnable takeTask() {
		return this.evictedQueue.poll();
	}

	protected abstract void notifyValueUpdate(K key, CacheValueEntity<V> entity, boolean replace);

	protected abstract boolean hasChanged(K key, V value);

	protected String getThreadAndTime() {
		return Thread.currentThread().getName() + "," + DateUtils.getHHMMSSFomrateTips();
	}

}
