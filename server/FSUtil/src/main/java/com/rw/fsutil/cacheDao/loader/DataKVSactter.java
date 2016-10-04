package com.rw.fsutil.cacheDao.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.cache.evict.EvictedUpdateTask;

/**
 * 把原来代码拷贝过来，还没整理
 * 
 * @param <T>
 */
public class DataKVSactter<T> extends PersistentLoader<String, T> {

	private final ClassInfo classInfo;
	private final JdbcTemplate template;
	private final String tableName;
	private final String updateSql;

	public DataKVSactter(ClassInfo classInfoPojo, JdbcTemplate template) {
		this.classInfo = classInfoPojo;
		this.template = template;
		this.tableName = classInfoPojo.getTableName();
		this.updateSql = "update " + tableName + " set dbvalue = ? where dbkey = ?";
	}

	@SuppressWarnings("unchecked")
	@Override
	public T load(String key) throws DataNotExistException, Exception {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String sql = "select dbvalue from " + classInfo.getTableName() + " where dbkey=?";
		List<String> result = template.queryForList(sql, String.class, key);

		if (result == null || result.isEmpty()) {
			throw new DataNotExistException();
		}
		return (T) classInfo.fromJson(result.get(0));
	}

	@Override
	public boolean delete(String key) throws DataNotExistException, Exception {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		String sql = "delete from " + classInfo.getTableName() + " where dbkey=?";
		int result = template.update(sql, key);
		return result > 0;
	}

	@Override
	public boolean insert(String key, T value) throws DuplicatedKeyException, Exception {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(classInfo.getTableName()).append(" (dbkey, dbvalue) values(?,?)");
		String writeValue = toJson(value);
		int affectedRows = template.update(sql.toString(), new Object[] { key, writeValue });
		// lida 2015-09-23 执行成功返回的结果是2
		return affectedRows > 0;
	}

	@Override
	public boolean updateToDB(String key, T value) {
		String sql = "update " + classInfo.getTableName() + " set dbvalue = ? where dbkey = ?";
		String writeValue = toJson(value);
		int affectedRows = template.update(sql, new Object[] { writeValue, key });
		// lida 2015-09-23 执行成功返回的结果是2
		return affectedRows > 0;
	}

	private String toJson(T t) {
		String json = null;
		try {
			json = classInfo.toJson(t);
		} catch (Exception e) {
			// 数据解释出错，不能往下继续，直接抛出RuntimeException给顶层捕获
			throw (new RuntimeException("DataKVDao[toJson] json转换异常", e));
		}
		return json;
	}

	@Override
	public String getTableName(String key) {
		return tableName;
	}

	@Override
	public Map<String, String> getUpdateSqlMapping() {
		HashMap<String, String> map = new HashMap<String, String>(2);
		map.put(tableName, updateSql);
		return map;
	}

	@Override
	public Object[] extractParams(String key, T value) {
		String writeValue = toJson(value);
		if (writeValue == null) {
			return null;
		}
		return new Object[] { writeValue, key };
	}

	@Override
	public boolean hasChanged(String key, T value, EvictedUpdateTask<String> evictedUpdateTask) {
		return true;
	}
}
