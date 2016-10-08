package com.rw.fsutil.cacheDao.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.cache.evict.EvictedUpdateTask;
import com.rw.fsutil.dao.kvdata.DataKvManager;
import com.rw.fsutil.dao.optimize.DataAccessFactory;

public class DataKVIntegration<T> extends PersistentLoader<String, T> {

	private final Integer type;
	private final ClassInfo classInfoPojo;
	private final JdbcTemplate template;
	private final String[] selectSqlArray;
	private final String[] delectSqlArray;
	private final String[] updateSqlArray;
	private final String[] tableNameArray;
	private final boolean forceUpdateOnEviction;
	private final int length;

	public DataKVIntegration(int type, ClassInfo classInfoPojo, JdbcTemplate template, boolean forceUpdateOnEviction) {
		this.type = type;
		this.classInfoPojo = classInfoPojo;
		this.template = template;
		DataKvManager dataKvManager = DataAccessFactory.getDataKvManager();
		this.selectSqlArray = dataKvManager.getSelectSqlArray();
		this.delectSqlArray = dataKvManager.getDeleteSqlArray();
		this.updateSqlArray = dataKvManager.getUpdateSqlArray();
		this.tableNameArray = dataKvManager.getTableNameArray();
		this.length = this.selectSqlArray.length;
		this.forceUpdateOnEviction = forceUpdateOnEviction;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T load(String key) throws DataNotExistException, Exception {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(key, length);
		String sql = selectSqlArray[tableIndex];
		List<String> result = template.queryForList(sql, String.class, key, type);
		if (result == null || result.isEmpty()) {
			throw new DataNotExistException();
		}
		return (T) classInfoPojo.fromJson(result.get(0));
	}

	@Override
	public boolean delete(String key) throws DataNotExistException, Exception {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(key, length);
		String sql = delectSqlArray[tableIndex];
		int result = template.update(sql, key, type);
		return result > 0;
	}

	@Override
	public boolean insert(String key, T value) throws DuplicatedKeyException, Exception {
		return false;
	}

	@Override
	public boolean updateToDB(String key, T value) {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(key, length);
		String sql = updateSqlArray[tableIndex];
		String writeValue = toJson(value);
		int affectedRows = template.update(sql, new Object[] { writeValue, key, type });
		// lida 2015-09-23 执行成功返回的结果是2
		return affectedRows > 0;
	}

	private String toJson(T t) {
		String json = null;

		try {
			json = classInfoPojo.toJson(t);
		} catch (Exception e) {
			// 数据解释出错，不能往下继续，直接抛出RuntimeException给顶层捕获
			throw (new RuntimeException("DataKVDao[toJson] json转换异常", e));
		}
		return json;
	}

	@Override
	public String getTableName(String key) {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(key, length);
		return tableNameArray[tableIndex];
	}

	@Override
	public Map<String, String> getUpdateSqlMapping() {
		HashMap<String, String> map = new HashMap<String, String>(tableNameArray.length);
		for (int i = tableNameArray.length; --i >= 0;) {
			map.put(tableNameArray[i], updateSqlArray[i]);
		}
		return map;
	}

	@Override
	public Object[] extractParams(String key, T value) {
		String writeValue = toJson(value);
		if (writeValue == null) {
			return null;
		}
		return new Object[] { writeValue, key, type };
	}

	@Override
	public boolean hasChanged(String key, T value) {
		return forceUpdateOnEviction;
	}

}
