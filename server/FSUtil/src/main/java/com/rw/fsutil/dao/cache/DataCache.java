package com.rw.fsutil.dao.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import com.rw.fsutil.dao.cache.record.CacheRecordEvent;
import com.rw.fsutil.dao.cache.record.RecordEvent;
import com.rw.fsutil.dao.cache.trace.CacheJsonConverter;
import com.rw.fsutil.dao.cache.trace.DataChangedListener;

/**
 * 数据缓存
 * 
 * @author Jamaz
 */
@SuppressWarnings("rawtypes")
public class DataCache<K, V> implements DataUpdater<K> {

	private static final Object PRESENT = new Object();
	private final ReentrantLock lock;
	private final LinkedHashMap<K, CacheValueEntity<V>> cache; // 数据集合
	private final ConcurrentHashMap<K, Object> delayUpdateMap; // 延迟更新任务，表示一个一段时间后执行的任务
	private final ConcurrentHashMap<K, Object> delayRemoveMap; // 延迟删除任务，表示一个一段时间后删除的任务
	private final ConcurrentHashMap<K, ReentrantFutureTask> taskMap; // 任务集合，表示一个以主键互斥的任务
	private final SimpleCache<K, Long> penetrationCache;// 缓存穿透，表示在数据库找不到的任务，缓存一段时间防止缓存穿透
	private final long penetrationTimeOutMillis;
	private final PersistentLoader<K, V> loader;
	private final int capacity;
	private final int updatePeriod;
	private final long timeoutNanos;
	private final ScheduledThreadPoolExecutor scheduledExecutor;
	private final DataNotExistHandler<K, V> dataNotExistHandler;
	private CacheLogger logger;
	private String name;
	private CacheJsonConverter<K, V, RecordEvent<?>> jsonConverter;
	private final AtomicLong generator;
	private final ArrayList<DataChangedListener> dataChangedListeners;

	public DataCache(Class clazz, int initialCapacity, int maxCapacity, int updatePeriod, ScheduledThreadPoolExecutor scheduledExecutor, PersistentLoader<K, V> loader, DataNotExistHandler<K, V> dataNotExistHandler, CacheJsonConverter<K, V, ? extends RecordEvent<?>> jsonConverter) {
		this.name = clazz.getSimpleName();
		this.capacity = maxCapacity;
		this.logger = CacheFactory.getLogger(name);
		this.lock = new ReentrantLock();
		this.scheduledExecutor = scheduledExecutor;
		this.updatePeriod = updatePeriod;
		this.delayUpdateMap = new ConcurrentHashMap<K, Object>(initialCapacity, 0.5f);
		this.delayRemoveMap = new ConcurrentHashMap<K, Object>(initialCapacity, 0.5f);
		this.taskMap = new ConcurrentHashMap<K, ReentrantFutureTask>();
		this.penetrationCache = new SimpleCache<K, Long>(1000);
		this.penetrationTimeOutMillis = TimeUnit.MINUTES.toMillis(5);
		this.loader = loader;
		this.generator = new AtomicLong();
		this.timeoutNanos = TimeUnit.SECONDS.toNanos(6);
		if (dataNotExistHandler != null) {
			this.dataNotExistHandler = dataNotExistHandler;
		} else {
			this.dataNotExistHandler = new DefualtDataNotExistHandler();
		}
		this.jsonConverter = (CacheJsonConverter<K, V, RecordEvent<?>>) jsonConverter;
		this.cache = new LRUCache(initialCapacity);
		this.dataChangedListeners = new ArrayList<DataChangedListener>(0);
		this.logger.info("create DAO = " + name + ",maxCapacity = " + maxCapacity + ",updatePeriod = " + updatePeriod);
	}

	class LRUCache extends LinkedHashMap<K, CacheValueEntity<V>> {

		private static final long serialVersionUID = 1L;

		public LRUCache(int initialCapacity) {
			super(initialCapacity, 0.5f, true);
		}

		@Override
		protected boolean removeEldestEntry(Entry<K, CacheValueEntity<V>> eldest) {
			if (size() <= capacity) {
				return false;
			}
			K key = eldest.getKey();
			logger.info("evict:" + key);
			CacheValueEntity<V> value = eldest.getValue();
			if (value.getState() == CacheValueState.DELETED) {
				// 已删除数据的正常移除
				logger.info("evict deleted:" + key);
				return true;
			}
			if (delayUpdateMap.containsKey(key) || delayRemoveMap.containsKey(key)) {
				// 有一种情况是添加失败的，就是这个数据刚被加载进来
				ReentrantFutureTask evictedTask = createTask(new EvictedTask(key, value, value.getState() == CacheValueState.MARK_DELETE));
				// 还没来得及执行finally的时候又被T走，这个可能性极低，而且也超过了缓存的负载能力
				if (DataCache.this.taskMap.putIfAbsent(key, evictedTask) != null) {
					logger.error("fatal@save evicted task fail:" + key);
				} else {
					logger.info("evict:" + key + "," + evictedTask);
					DataCache.this.scheduledExecutor.schedule(evictedTask, 0, TimeUnit.NANOSECONDS);
				}
			}
			return true;
		}
	}

	class DefualtDataNotExistHandler implements DataNotExistHandler<K, V> {

		@Override
		public V callInLoadTask(K key) {
			// 缓存穿透处理
			logger.info("load not exist data:" + key);
			penetrationCache.put(key, System.currentTimeMillis());
			return null;
		}

	}

	/**
	 * 获取指数据，如果不存在，尝试从数据库中加载该主键对应的数据
	 * 
	 * @param key
	 *            数据的主键
	 * @return 指定的数据
	 * @throws InterruptedException
	 *             在加载的过程中可能抛出{@link InterruptedException} ，但此时不能确定数据是否已经加载完成
	 * @throws Throwable
	 *             在加载的过程中可能抛出其他的异常，需要自己进行捕捉
	 */
	public V getOrLoadFromDB(K key) throws InterruptedException, Throwable {
		// 先从缓存中获取，如果存在于缓存，直接返回
		CacheValueEntity<V> cacheValue = getOrLoadCacheFromDB(key);
		return cacheValue == null ? null : cacheValue.getValue();
	}

	public CacheValueEntity<V> getOrLoadCacheFromDB(K key) throws InterruptedException, Throwable {
		// 先从缓存中获取，如果存在于缓存，直接返回
		lock.lock();
		try {
			CacheValueEntity<V> value = this.cache.get(key);
			if (value != null) {
				// return isDeleted(value) ? null : value.value;
				return isDeleted(value) ? null : value;
			}
		} finally {
			lock.unlock();
		}

		// 命中缓存穿透
		// TODO 这里可用LRU代替
		if (penetrationCache != null) {
			Long time = this.penetrationCache.get(key);
			if (time != null) {
				if (System.currentTimeMillis() - time < penetrationTimeOutMillis) {
					return null;
				} else {
					this.penetrationCache.remove(key);
				}
			}
		}
		// 从数据库加载
		return loadFromDB(key, CacheValueState.PERSISTENT, new CacheStackTrace());
	}

	private CacheValueEntity<V> loadFromDB(K key, CacheValueState state, CacheStackTrace trace) throws InterruptedException, TimeoutException, Throwable {
		// 创建加载任务
		ReentrantFutureTask<CacheValueEntity<V>> task = createTask(new LoadTask(key, state, trace));
		long lastTime = System.nanoTime();
		long remainNanos = timeoutNanos;
		for (;;) {
			ReentrantFutureTask oldTask = submitKeyTask(key, task);
			if (oldTask == null) {
				task.run();
				try {
					return task.get(remainNanos, TimeUnit.NANOSECONDS);
				} catch (TimeoutException ex) {
					logger.error("loadFromDB timeout:" + TimeUnit.NANOSECONDS.toMillis(remainNanos) + "," + task, ex);
					throw ex;
				} catch (ExecutionException ex) {
					throw ex.getCause();
				}
			}
			long now = System.nanoTime();
			remainNanos -= (now - lastTime);
			lastTime = now;
			Object result = null;
			try {
				result = oldTask.get(remainNanos, TimeUnit.NANOSECONDS);
			} catch (TimeoutException ex) {
				logger.error("wait for other task loadFromDB timeout:" + TimeUnit.NANOSECONDS.toMillis(remainNanos) + "," + oldTask, ex);
				throw ex;
			} catch (ExecutionException ex) {
				// ignore
			}
			now = System.nanoTime();
			remainNanos -= (now - lastTime);
			lastTime = now;
			if (result == null || result instanceof Boolean) {
				continue;
			}
			lock.lock();
			try {
				CacheValueEntity<V> value = this.cache.get(key);
				if (value != null) {
					return isDeleted(value) ? null : value;
				}
			} finally {
				lock.unlock();
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
		lock.lock();
		try {
			CacheValueEntity<V> old = this.cache.get(key);
			if (old != null) {
				if (isDeleted(old)) {
					logger.warn("data has delete:" + key);
					return false;
				}
				// 标记为已经被删除
				old.setCacheValueState(CacheValueState.MARK_DELETE);
				// 提交删除任务
				boolean submit = submitRemoveTask(key);
				logger.info("mark delete:" + key + "," + submit, true);
				return true;
			}
		} finally {
			lock.unlock();
		}
		// 不存在必须去数据库查一次
		logger.info("load data by delete operation:" + key, true);
		CacheValueEntity<V> cacheValue = loadFromDB(key, CacheValueState.MARK_DELETE, new CacheStackTrace());
		if (cacheValue == null) {
			return false;
		}
		// 提交删除任务
		submitRemoveTask(key);
		return true;
	}

	/**
	 * 从缓存中获取指定数据
	 * 
	 * @param key
	 * @return
	 */
	public V getFromMemory(K key) {
		lock.lock();
		try {
			CacheValueEntity<V> value = this.cache.get(key);
			if (value == null || isDeleted(value)) {
				return null;
			}
			// return value.value;
			return value.getValue();
		} finally {
			lock.unlock();
		}
	}

	private RecordEvent<?> toJson(K key, V value) {
		if (!CacheLoggerSwitch.getInstance().isCacheLogger(name)) {
			return null;
		}
		CacheJsonConverter<K, V, ?> converter = DataCache.this.jsonConverter;
		if (converter == null) {
			return null;
		}
		try {
			return converter.parseToRecordData(key, value);
		} catch (Exception e) {
			// logger.error("parse json exception:" + key, e);
			e.printStackTrace();
			return null;
		}
	}

	private CacheValueEntity<V> update(final K key, final V value) throws DataDeletedException {
		CacheValueEntity<V> old;
		boolean updateDelData = false;
		lock.lock();
		try {
			old = this.cache.get(key);
			if (old == null) {
				return null;
			}
			if (isDeleted(old)) {
				updateDelData = true;
			} else {
				old.setValue(value);
			}
		} finally {
			lock.unlock();
		}
		if (updateDelData) {
			logger.error("insert delete data:" + key + "," + value);
			throw new DataDeletedException();
		}
		// 此Json可能是一个中间值
		CacheStackTrace trace = new CacheStackTrace();
		record(key, value, old, trace);
		this.submitUpdateTask(key, trace);
		return old;
	}

	private void record(K key, V value, CacheValueEntity<V> old, CacheStackTrace trace) {
		// 当前json
		RecordEvent<?> jsonMap = toJson(key, value);
		if (jsonMap == null) {
			return;
		}
		for (int j = 0; j < 10; j++) {
			RecordEvent<?> lastRecord = old.get();
			CacheRecordEvent recordEvent = jsonConverter.parse(lastRecord, jsonMap);
			if (recordEvent == null) {
				System.out.println("无效commit:" + name);
				return;
			}
			if (old.compareAndSet(lastRecord, jsonMap)) {
				// TODO 未解决顺序问题，结合打日志程模型一起改，现在日志是慢速的
				logger.executeAysnEvent(CacheLoggerPriority.INFO, "U", recordEvent, trace);
				for (int i = this.dataChangedListeners.size(); --i >= 0;) {
					DataChangedListener listener = dataChangedListeners.get(i);
					try {
						listener.notifyDataChanged(recordEvent);
					} catch (Throwable t) {
						logger.error("notify data changed exception:", t);
					}
				}
				return;
			}
		}
		logger.error("record loop too much:" + name);
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
		lock.lock();
		try {
			if (this.cache.containsKey(key)) {
				return false;
			}
		} finally {
			lock.unlock();
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
		lock.lock();
		try {
			if (this.cache.containsKey(key)) {
				return false;
			}
		} finally {
			lock.unlock();
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
			ReentrantFutureTask task = getControlRight(key, new InsertTask(key, value, false, trace));
			task.run();
			return task.get() == null;
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
			ReentrantFutureTask old = submitKeyTask(key, insertTask);
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

	private ReentrantFutureTask submitKeyTask(K key, ReentrantFutureTask task) throws InterruptedException, TimeoutException {
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
		if (otherTask.getRunner() == Thread.currentThread()) {
			logger.warn("run in same thread:" + otherTask + "," + task);
			task.setUnderControl();
			return null;
		}

		// 这里执行其他操作可能会加载到内存
		try {
			otherTask.get(timeoutNanos, TimeUnit.SECONDS);
		} catch (ExecutionException ex) {
			// ignore
		} catch (TimeoutException e) {
			logger.error("wait task timeout:" + otherTask, e);
			throw e;
		}
		return otherTask;
	}

	/**
	 * 检测是否含有该键对应的数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key) {
		lock.lock();
		try {
			return this.cache.containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	private ReentrantFutureTask getControlRight(K key, TaskCallable task) throws InterruptedException, TimeoutException {
		ReentrantFutureTask t = new ReentrantFutureTask(task);
		for (;;) {
			if (submitKeyTask(key, t) == null) {
				return t;
			}
		}
	}

	/** 构造一个ReentrantFutureTask **/
	private ReentrantFutureTask createTask(TaskCallable task) {
		ReentrantFutureTask t = new ReentrantFutureTask(task);
		return t;
	}

	/** 提交更新任务 **/
	private void submitUpdateTask(K key, int second, Callable callable) {
		if (this.delayUpdateMap.putIfAbsent(key, PRESENT) == null) {
			scheduledExecutor.schedule(callable, second, TimeUnit.SECONDS);
		}
		CacheValueEntity<V> entity = this.get(key);
		if (entity != null) {
			record(key, entity.getValue(), entity, new CacheStackTrace());
		}
	}

	/** 提交更新任务 **/
	public void submitUpdateTask(K key) {
		submitUpdateTask(key, updatePeriod, new UpdateTask(key));
	}

	/** 提交更新任务 **/
	public void submitUpdateTask(K key, CacheStackTrace trace) {
		submitUpdateTask(key, updatePeriod, new UpdateTask(key, trace));
	}

	/** 提交删除任务 **/
	private boolean submitRemoveTask(K key, RemoveTask removeTask) {
		if (this.delayRemoveMap.putIfAbsent(key, PRESENT) == null) {
			scheduledExecutor.schedule(new RemoveOperation(key, removeTask), updatePeriod, TimeUnit.SECONDS);
			return true;
		} else {
			return false;
		}
	}

	private boolean submitRemoveTask(K key) {
		return submitRemoveTask(key, null);
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

			RecordEvent json = toJson(key, value);
			boolean insertFail = false;
			CacheValueEntity<V> cacheValue = new CacheValueEntity<V>(value, CacheValueState.PERSISTENT, json);
			CacheValueEntity<V> oldValue;
			lock.lock();
			try {
				oldValue = DataCache.this.cache.get(key);
				if (oldValue == null) {
					// 更新到缓存
					DataCache.this.cache.put(key, cacheValue);
				} else {
					insertFail = true;
				}
			} finally {
				lock.unlock();
			}
			if (insertFail) {
				logger.error("put into cache exist data:" + key);
			}
			// 更新缓存中的值，并提交更新任务
			// 提交一个打印任务
			// TODO 这里没有描述出变化的状态
			if (json != null) {
				logger.executeAysnEvent(CacheLoggerPriority.INFO, "I", json, trace);
			}
			// 缓存有旧值
			if (oldValue != null || duplicatedKey) {
				logger.error("insert cache fail:" + insertDB + "," + key + ",exist=" + duplicatedKey + "," + oldValue + "," + value);
				DataCache.this.submitUpdateTask(key);
			}
			return oldValue;
		}

		@Override
		public String getErrorInfo() {
			return "InsertTask移除失败:" + key;
		}

		@Override
		public String getName() {
			return "insertTask";
		}
	}

	/** 加载任务 **/
	class LoadTask extends TaskCallable<CacheValueEntity<V>> {

		private final CacheValueState state;
		private final CacheStackTrace trace;

		public LoadTask(K key, CacheValueState state, CacheStackTrace trace) {
			super(key);
			this.state = state;
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
				RecordEvent json = toJson(key, v);
				// CacheValueRecord record;
				// if (json != null) {
				// record = new CacheValueRecord(key, 1, new CacheStackTrace(),
				// json, null);
				// } else {
				// record = null;
				// }
				CacheValueEntity<V> cacheValue = new CacheValueEntity<V>(v, state, json);
				boolean needRecord = false;
				lock.lock();
				try {
					CacheValueEntity<V> old = cache.get(key);
					if (old == null) {
						cache.put(key, cacheValue);
						needRecord = true;
						return cacheValue;
					}
					logger.info("load data exsit:" + key + "," + old.getState() + "," + state + "," + old.getValue());
					// 到这里表示，从内存命中失败后，到执行这个loadTask(包括插入)之间
					// 已经完成了一次loadTask的执行(可能是get发起的或者remove发起的)
					if (isDeleted(old)) {
						// 数据已被删除
						logger.warn("load delete data:" + key + "," + v);
						return null;
					}
					// 如果是删除操作发起的加载，要在lock的情况下设置MARK_DELETE标志
					if (state == CacheValueState.MARK_DELETE) {
						old.setCacheValueState(state);
					}
					return old;
				} finally {
					lock.unlock();
					if (json != null && needRecord) {
						logger.executeAysnEvent(CacheLoggerPriority.INFO, "L", json, this.trace);
					}
				}
			} else {
				// logger.warn("加载数据为null:" + key + "," + name);
				return null;
			}
		}

		@Override
		public String getErrorInfo() {
			return "LoadTask fail:" + key + "," + state;
		}

		@Override
		public String getName() {
			return "loadTask:" + state;
		}
	}

	/** LRU移除任务 **/
	class EvictedTask extends TaskCallable<V> {

		private final CacheValueEntity<V> value;
		private final boolean remove;

		public EvictedTask(K key, CacheValueEntity<V> value, boolean remove) {
			super(key);
			this.value = value;
			this.remove = remove;
		}

		@Override
		public String getErrorInfo() {
			return "evict fail:" + key + "," + value + "," + remove;
		}

		@Override
		public V call() throws Exception {
			if (!remove) {
				Object exist = DataCache.this.delayUpdateMap.remove(key);
				logger.info("evict_update:" + key + "," + (exist != null));
				// if (!loader.updateToDB(key, value.value)) {
				if (!loader.updateToDB(key, value.getValue())) {
					logger.error("fatal@evict_update fail:" + key + "," + value.getValue());
				}
				return null;
			}
			if (DataCache.this.delayRemoveMap.remove(key) == null) {
				// 只用一种可能，在LRU的时候判断到MARK_DELETE状态到提交任务之间，执行了一个完整的RemoveOpertaion
				logger.warn("remove operation has been executed@EvictedTask" + key);
				return null;
			}
			logger.info("evict_remove:" + key);
			boolean result = loader.delete(key);
			if (!result) {
				logger.error("fatal@evict_remove faile:" + key);
			}
			return null;
		}

		@Override
		public String getName() {
			return "evcitTask";
		}
	}

	class RemoveOperation implements Callable {

		private final K key;
		private volatile RemoveTask removeTask;

		public RemoveOperation(K key) {
			this.key = key;
		}

		public RemoveOperation(K key, RemoveTask removeTask) {
			this.key = key;
			this.removeTask = removeTask;
		}

		@Override
		public Object call() {
			// 更新任务必须保证互斥
			if (DataCache.this.delayRemoveMap.remove(key) == null) {
				logger.info("remove operation has been executed@RemoveOperation" + key);
				return null;
			}
			if (removeTask == null) {
				removeTask = new RemoveTask(key, 0);
			}
			ReentrantFutureTask<V> task;
			try {
				task = getControlRight(key, removeTask);
				task.run();
				return task.get();
			} catch (InterruptedException e) {
				logger.error("remove operation interrupted:" + key, e);
				submitRemoveTask(key, removeTask);
			} catch (TimeoutException e) {
				logger.error("remove operation is timeout:" + key, e);
				submitRemoveTask(key, removeTask);
			} catch (ExecutionException e) {
				logger.error("remove operation exception:" + key, e);
				submitRemoveTask(key, removeTask);
			}
			return null;
		}
	}

	/** 移除任务 **/
	class RemoveTask extends TaskCallable<Boolean> {

		private volatile int times;

		public RemoveTask(K key, int times) {
			super(key);
			this.times = times;
		}

		@Override
		public Boolean call() throws Exception {
			// 这个时候其他线程无法加载从数据库加载的任务
			CacheValueEntity<V> value = DataCache.this.get(key);
			// 数据存在缓存时三种情况的处理
			boolean isInCache = (value != null);
			if (isInCache) {
				CacheValueState state = value.getState();
				if (state == CacheValueState.DELETED) {
					logger.error("RemoveTask数据已被删除:" + key);
					return Boolean.FALSE;
				}
				// 另外两种情况都需要删除
				if (state == CacheValueState.PERSISTENT) {
					logger.error("RemoveTask状态错误:" + key);
					lock.lock();
					try {
						value = DataCache.this.cache.get(key);
						isInCache = (value != null);
						if (isInCache) {
							state = value.getState();
							logger.error("lock重读状态:" + key + "," + isInCache + "," + state);
							if (state == CacheValueState.DELETED) {
								return Boolean.FALSE;
							}

							if (state == CacheValueState.PERSISTENT) {
								value.setCacheValueState(CacheValueState.MARK_DELETE);
								logger.error("重置删除状态:" + key);
							}
						}
					} finally {
						lock.unlock();
					}
				}
				// CacheValueState.MARK_DELETE属于正常状态
			}
			try {
				if (loader.delete(key)) {
					// TODO 考虑是否必须从缓存删除
					// 可以防止缓存穿透
					// 一个已被删除的数据，更新时可以提示失败
					if (isInCache) {
						value.setCacheValueState(CacheValueState.DELETED);
					}
					logger.info("delete success:" + key + "," + isInCache);
					return Boolean.TRUE;
				} else {
					logger.error("删除数据失败:" + key + ",第[" + (++times) + "]次," + isInCache);
					// 重新更新任务，不在缓存并且删除失败，表示被T除任务删除了
					if (isInCache) {
						submitRemoveTask(key, this);
					}
					return Boolean.FALSE;
				}
			} catch (DataNotExistException exception) {
				logger.error("删除数据不存在:" + key + "," + isInCache);
				return Boolean.FALSE;
			}
		}

		@Override
		public String getErrorInfo() {
			return "RemoveTask移除失败:" + key;
		}

		@Override
		public String getName() {
			return "removeTask";
		}
	}

	/**
	 * 更新任务
	 **/
	class UpdateTask implements Callable {

		private final K key;
		private volatile int times;
		private final CacheStackTrace trace;
		private final long version;

		public UpdateTask(K key) {
			this.key = key;
			this.trace = new CacheStackTrace();
			this.version = generator.incrementAndGet();
		}

		public UpdateTask(K key, CacheStackTrace trace) {
			this.key = key;
			this.trace = trace;
			this.version = generator.incrementAndGet();
		}

		@Override
		public Object call() throws Exception {
			// 更新任务必须保证互斥
			if (DataCache.this.delayUpdateMap.remove(key) == null) {
				logger.warn("updatetask executed:" + key + "," + name);
				return null;
			}
			CacheValueEntity<V> value = DataCache.this.get(key);
			if (value == null) {
				String taskName = null;
				ReentrantFutureTask task = taskMap.get(key);
				if (task != null) {
					taskName = task.getTask().getClass().getName();
				}
				logger.warn("update fail:" + key + "," + name + "," + CacheFactory.getStackTrace(trace) + ",version = " + version + "," + taskName);
				return null;
			}
			V v = value.getValue();
			if (v == null) {
				logger.error("update value is null:" + key + "," + value + "," + name);
				return null;
			}
			CacheValueState state = value.getState();
			if (state != CacheValueState.PERSISTENT) {
				logger.info("update delete data:" + key + "," + state);
				return null;
			}
			// RecordEvent record = value.get();
			// long version;
			// if (record != null) {
			// version = record.getVersion();
			// } else {
			// version = 0;
			// }
			try {
				if (!loader.updateToDB(key, v)) {
					times++;
					if (times < 3 || (times % 10 == 0)) {
						logger.error("update fail:" + key + ",第[" + times + "]次," + version + "," + name);
					}
					int period;
					if (times < 2) {
						period = updatePeriod;
					} else if (times < 10) {
						period = updatePeriod << 1;
					} else if (times < 20) {
						period = updatePeriod * 3;
					} else {
						period = updatePeriod * 10;
					}
					// submitUpdateTask(key, period, this);
					if (delayUpdateMap.putIfAbsent(key, PRESENT) == null) {
						scheduledExecutor.schedule(this, period, TimeUnit.SECONDS);
					}
				} else {
					logger.info("UpdateTask:" + key + "," + version);
				}
			} catch (Throwable t) {
				logger.error("update exception:" + key + "," + version, t);
				t.printStackTrace();
			}
			return null;
		}
	}

	private CacheValueEntity<V> get(K key) {
		lock.lock();
		try {
			return this.cache.get(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取缓存当前的大小
	 * 
	 * @return
	 */
	public int size() {
		lock.lock();
		try {
			return this.cache.size();
		} finally {
			lock.unlock();
		}
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
	 * 返回缓存值集合的拷贝
	 * 
	 * @return
	 */
	public List<V> values() {
		lock.lock();
		try {
			int size = this.cache.size();
			ArrayList<V> list = new ArrayList<V>(size);
			for (CacheValueEntity<V> value : this.cache.values()) {
				// list.add(value.value);
				list.add(value.getValue());
			}
			return list;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 返回缓存键集合的拷贝
	 * 
	 * @return
	 */
	public List<K> keys() {
		lock.lock();
		try {
			return new ArrayList<K>(this.cache.keySet());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取缓存key-value的拷贝
	 * 
	 * @return
	 */
	public Map<K, V> entries() {
		lock.lock();
		try {
			int size = this.cache.size();
			LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(size);
			for (Map.Entry<K, CacheValueEntity<V>> entry : this.cache.entrySet()) {
				// map.put(entry.getKey(), entry.getValue().value);
				map.put(entry.getKey(), entry.getValue().getValue());
			}
			return map;
		} finally {
			lock.unlock();
		}
	}

	public PersistentLoader<K, V> getLoader() {
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

	private boolean isDeleted(CacheValueEntity<V> value) {
		// CacheValueState state = value.state;
		CacheValueState state = value.getState();
		return state == CacheValueState.MARK_DELETE || state == CacheValueState.DELETED;
	}

	static enum CacheValueState {
		/**
		 * 持久化的
		 */
		PERSISTENT,
		/**
		 * 在缓存标记为删除
		 */
		MARK_DELETE,
		/**
		 * 已从DB删除
		 */
		DELETED
	}

	abstract class TaskCallable<T> implements Callable<T> {

		final K key;

		public TaskCallable(K key) {
			this.key = key;
		}

		public abstract String getName();

		public abstract String getErrorInfo();
	}

	private class ReentrantFutureTask<V> extends FutureTask<V> {

		private final TaskCallable task;
		private volatile Thread runner;
		private volatile boolean controller;

		public ReentrantFutureTask(TaskCallable<V> task) {
			super(task);
			this.task = task;
			this.controller = true;
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
						logger.error("fatal@" + task.getErrorInfo());
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

		public TaskCallable getTask() {
			return task;
		}

	}

	private Map<K, CacheValueEntity<V>> getCopy() {
		lock.lock();
		try {
			return new LinkedHashMap<K, CacheValueEntity<V>>(this.cache);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<Runnable> createUpdateTask() {
		final Map<K, CacheValueEntity<V>> map = getCopy();
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
						CacheValueState state = cacheValue.getState();
						if (state == CacheValueState.DELETED) {
							// 数据已被删除不作处理
							logger.info("忽略Future过期数据:" + key + "," + value);
						} else if (state == CacheValueState.MARK_DELETE) {
							logger.info("执行Future删除操作:" + key + "," + value);
							// 执行删除操作
							RemoveTask removeTask = new RemoveTask(key, 0);
							ReentrantFutureTask<V> task = getControlRight(key, removeTask);
							task.run();
							task.get();
						} else {
							logger.info("执行Future更新操作:" + key + "," + value);
							if (!loader.updateToDB(key, value)) {
								logger.error("执行Future更新失败:" + key);
							}
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

}
