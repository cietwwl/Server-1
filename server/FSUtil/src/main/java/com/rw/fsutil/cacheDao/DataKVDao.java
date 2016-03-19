package com.rw.fsutil.cacheDao;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassHelper;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.CacheValueEntity;
import com.rw.fsutil.dao.cache.DataCache;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.DataDeletedException;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.LRUCacheListener;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.common.DBThreadPoolMgr;
import com.rw.fsutil.dao.common.JdbcTemplateFactory;
import com.rw.fsutil.log.SqlLog;
import com.rw.fsutil.util.SpringContextUtil;

/**
 * 前台数据(支持单表和多表) 数据库+memcached
 * 
 * @author Ace
 * @refactor Jamaz
 * @param <T>
 * @param <ID>
 */
public class DataKVDao<T> {

	private final ClassInfo classInfoPojo;
	
	private final DataCache<String, T> cache;
	
	private JdbcTemplate template = null;
	public DataKVDao(Class<T> clazz) {
		try {
			this.classInfoPojo = new ClassInfo(clazz);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError("初始化ClassInfo失败："+clazz);
		}
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		this.template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		int cacheSize = getCacheSize();
//		this.cache = new DataCache<String, T>(clazz.getName(), cacheSize, cacheSize, getUpdatedSeconds(), DBThreadPoolMgr.getExecutor(), loader, cacheListener);
		this.cache = DataCacheFactory.createDataDache(clazz.getSimpleName(), cacheSize, cacheSize, getUpdatedSeconds(), loader);
	}
	
	public DataKVDao() {
		Class<T> clazz = ClassHelper.getEntityClass(this.getClass());
		try {
			this.classInfoPojo = new ClassInfo(clazz);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError("初始化ClassInfo失败："+clazz);
		}
		DruidDataSource dataSource = SpringContextUtil.getBean("dataSourceMT");
		this.template = JdbcTemplateFactory.buildJdbcTemplate(dataSource);
		int cacheSize = getCacheSize();
//		this.cache = new DataCache<String, T>(clazz.getName(), cacheSize, cacheSize, getUpdatedSeconds(), DBThreadPoolMgr.getExecutor(), loader, cacheListener);
		this.cache = DataCacheFactory.createDataDache(clazz.getSimpleName(), cacheSize, cacheSize, getUpdatedSeconds(), loader);
	}
	
	private LRUCacheListener<String,T> cacheListener = new LRUCacheListener<String,T>() {

		@Override
		public void notifyElementEvicted(String key, T value) {
		}
	};

	private PersistentLoader<String, T> loader = new PersistentLoader<String, T>(){

		@Override
		public T load(String key) throws DataNotExistException, Exception {
			if (StringUtils.isBlank(key)) {
				return null;
			}
			String sql = "select dbvalue from " + classInfoPojo.getTableName() + " where dbkey=?";
			String value = null;
			List<String> result = template.queryForList(sql, String.class, key);
			if (result != null && result.size() > 0) {
				value = new String(result.get(0));
				T t = toT(value);
				return t;
			}
			return null;
		}

		@Override
		public boolean delete(String key) throws DataNotExistException, Exception {
			if (StringUtils.isBlank(key)) {
				return false;
			}
			String sql = "delete from " + classInfoPojo.getTableName() + " where dbkey=?";
			int result = template.update(sql, key);
			return result > 0;
		}

		@Override
		public boolean insert(String key, T value) throws DuplicatedKeyException, Exception {
			if(StringUtils.isBlank(key)){
				return false;
			}
			StringBuilder sql = new StringBuilder();
			sql.append("insert into ").append(classInfoPojo.getTableName()).append(" (dbkey, dbvalue) values(?,?)");
			// this.template.update(sql.toString(),new Object[]{key, HexUtil.bytes2HexStr(value.getBytes("UTF-8"))});
			String writeValue = toJson(value);
			int affectedRows = template.update(sql.toString(), new Object[] { key, writeValue });
			//lida 2015-09-23 执行成功返回的结果是2
			return affectedRows > 0;
		}

		@Override
		public boolean updateToDB(String key, T value) {
			String sql = "update "+ classInfoPojo.getTableName() + " set dbvalue = ? where dbkey = ?";
			String writeValue = toJson(value);
			int affectedRows = template.update(sql.toString(), new Object[] {writeValue,key});
			//lida 2015-09-23 执行成功返回的结果是2
			return affectedRows > 0;
		}
		
	};
	
	public void update(String id){
		cache.submitUpdateTask(id);
	}

	public boolean update(T t) {
		String id = getId(t);
		if(!StringUtils.isNotBlank(id)){
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


	public String getId(T t){
		Field idField = classInfoPojo.getIdField();
		String id = null;
		try {
			id = String.valueOf(idField.get(t));
		} catch (Exception e) {
			SqlLog.error(e);
		} 
		return  id;
	};


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
			if(entity == null){
				return null;
			}
			return entity.getValue();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	private String toJson(T t) {
		String json = null;
		
		try {
			json = classInfoPojo.toJson(t);
		} catch (Exception e) {
			//数据解释出错，不能往下继续，直接抛出RuntimeException给顶层捕获
			throw(new RuntimeException("DataKVDao[toJson] json转换异常", e));
		}	
		return json;
	}

	@SuppressWarnings("unchecked")
	private T toT(String value) {
		T t = null;
		if (StringUtils.isNotBlank(value)) {
			try {
				t = (T) classInfoPojo.fromJson(value);
			} catch (Exception e) {
				//数据解释出错，不能往下继续，直接抛出RuntimeException给顶层捕获
				throw(new RuntimeException("DataKVDao[toT] json转换异常", e));
			}				
			
		}
		return t;
	}
	

	@SuppressWarnings("unchecked")
	public Class<T> getEntityClass() {
		return (Class<T>) classInfoPojo.getClazz();
	}
	
	/**
	 * 获取缓存数量大小
	 * @return
	 */
	protected int getCacheSize(){
		return 3000;
	}
	
	/**
	 * 获取更新周期间隔(单位：秒)
	 * @return
	 */
	protected int getUpdatedSeconds(){
		return 60;
	}
}
