package com.rw.fsutil.cacheDao;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.cacheDao.loader.DataKVIntegration;
import com.rw.fsutil.cacheDao.loader.DataKVSactter;
import com.rw.fsutil.cacheDao.loader.DataKvNotExistHandler;
import com.rw.fsutil.dao.annotation.ClassHelper;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.CacheValueEntity;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataDeletedException;
import com.rw.fsutil.dao.cache.DataNotExistHandler;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessSimpleSupport;
import com.rw.fsutil.log.SqlLog;

/**
 * 前台数据(支持单表和多表) 数据库+memcached
 * 
 * @author Ace
 * @param <T>
 * @param <ID>
 */
public class DataKVDao<T> {

	private final ClassInfo classInfo;
	private final DataCache<String, T> cache;
	private final JdbcTemplate template;
	private final Integer type;

	public DataKVDao(Class<T> clazz) {
		this.classInfo = new ClassInfo(clazz);
		DataAccessSimpleSupport simpleSupport = DataAccessFactory.getSimpleSupport();
		this.template = simpleSupport.getMainTemplate();
		int cacheSize = getCacheSize();
		this.cache = DataCacheFactory.createDataDache(clazz, cacheSize, cacheSize, getUpdatedSeconds(), new DataKVSactter<T>(classInfo, template), new ObjectConvertor<T>());
		this.type = null;
	}

	public DataKVDao() {
		this.classInfo = new ClassInfo(ClassHelper.getEntityClass(getClass()));
		this.template = DataAccessFactory.getSimpleSupport().getMainTemplate();
		int cacheSize = getCacheSize();
		Class<? extends DataKVDao<T>> clazz = (Class<? extends DataKVDao<T>>) getClass();
		this.type = DataAccessFactory.getDataKvManager().getDataKvType(clazz);
		PersistentLoader<String, T> persistentLoader;
		if (this.type == null) {
			persistentLoader = new DataKVSactter<T>(classInfo, template);
		} else {
			persistentLoader = new DataKVIntegration<T>(type, classInfo, template);
		}
		final DataExtensionCreator<T> creator = DataAccessFactory.getDataKvManager().getCreator(clazz);
		DataNotExistHandler<String, T> handler;
		if (creator == null) {
			handler = null;
		} else {
			handler = new DataKvNotExistHandler<T>(type, creator, classInfo);
		}
		this.cache = DataCacheFactory.createDataDache(classInfo.getClazz(), cacheSize, cacheSize, getUpdatedSeconds(), persistentLoader, handler, new ObjectConvertor<T>());
	}

	/**
	 * 这个重载的方法是用来提交数据的！
	 * 
	 * @param id
	 */
	public void update(String id) {
		cache.submitUpdateTask(id);
	}

	public boolean commit(T t) {
		if (update(t)) {
			String id = getId(t);
			// System.out.println("commit id=" + id);
			update(id);
			return true;
		}
		return false;
	}

	public boolean update(T t) {
		String id = getId(t);
		if (!StringUtils.isNotBlank(id)) {
			return false;
		}
		try {
			cache.put(id, t);
			return true;
		} catch (DataDeletedException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getId(T t) {
		Field idField = classInfo.getIdField();
		String id = null;
		try {
			id = String.valueOf(idField.get(t));
		} catch (Exception e) {
			SqlLog.error(e);
		}
		return id;
	}

	public boolean delete(String id) {
		try {
			return this.cache.removeAndUpdateToDB(id);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public T get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		try {
			CacheValueEntity<T> entity = this.cache.getOrLoadCacheFromDB(id);
			if (entity == null) {
				return null;
			}
			return entity.getValue();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 尝试从内存获取对象
	 * 
	 * @param id
	 * @return
	 */
	public T getFromMemory(String id) {
		return this.cache.getFromMemory(id);
	}

	/**
	 * <pre>
	 * 当缓存中不存在指定键值对时，插入一个指定键值对到缓存，但不会发起数据库操作
	 * </pre>
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean putIntoCache(String key, T value) {
		return this.cache.preInsertIfAbsent(key, value);
	}

	/**
	 * <pre>
	 * 当缓存中不存在指定键值对时，通过dbString生成对应的对象插入缓存
	 * 不会发起dbString
	 * </pre>
	 * 
	 * @param key
	 * @param dbString
	 * @return
	 */
	public boolean putIntoCacheByDBString(String key, final String dbString) {
		return this.cache.preInsertIfAbsent(key, new Callable<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T call() throws Exception {
				return (T) classInfo.fromJson(dbString);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Class<T> getEntityClass() {
		return (Class<T>) classInfo.getClazz();
	}

	public ClassInfo getClassInfo() {
		return this.classInfo;
	}

	/**
	 * 获取缓存数量大小
	 * 
	 * @return
	 */
	protected int getCacheSize() {
		return DataAccessFactory.getDataKvManager().getDataKvCapacity();
	}

	/**
	 * 获取更新周期间隔(单位：秒)
	 * 
	 * @return
	 */
	protected int getUpdatedSeconds() {
		return 60;
	}

}
