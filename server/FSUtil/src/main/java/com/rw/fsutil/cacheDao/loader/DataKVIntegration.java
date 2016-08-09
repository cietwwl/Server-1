package com.rw.fsutil.cacheDao.loader;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.fsutil.dao.optimize.DataAccessStaticSupport;

public class DataKVIntegration<T> implements PersistentLoader<String, T> {

	private final Integer type;
	private final ClassInfo classInfoPojo;
	private final JdbcTemplate template;
	private final String[] selectSqlArray;
	private final String[] delectSqlArray;
	private final String[] updateSqlArray;
	private final int length;

	public DataKVIntegration(int type, ClassInfo classInfoPojo, JdbcTemplate template) {
		this.type = type;
		this.classInfoPojo = classInfoPojo;
		this.template = template;
		List<String> tableNameList = DataAccessStaticSupport.getDataKVTableNameList(template);
		this.length = tableNameList.size();
		this.selectSqlArray = new String[this.length];
		this.delectSqlArray = new String[this.length];
		this.updateSqlArray = new String[this.length];
		for (int i = 0; i < this.length; i++) {
			String tableName = tableNameList.get(i);
			selectSqlArray[i] = "select dbvalue from " + tableName + " where dbkey=? and type=?";
			delectSqlArray[i] = "delete from " + tableName + " where dbkey=? and type=?";
			updateSqlArray[i] = "update " + tableName + " set dbvalue=? where dbkey=? and type=?";
		}
	}

	@Override
	public T load(String key) throws DataNotExistException, Exception {
		int tableIndex = DataAccessFactory.getSimpleSupport().getTableIndex(key, length);
		String sql = selectSqlArray[tableIndex];
		List<String> result = template.queryForList(sql, String.class, key, type);
		if (result == null || result.isEmpty()) {
			throw new DataNotExistException();
		}
		return toT(result.get(0));
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

	@SuppressWarnings("unchecked")
	private T toT(String value) {
		T t = null;
		if (StringUtils.isNotBlank(value)) {
			try {
				t = (T) classInfoPojo.fromJson(value);
			} catch (Exception e) {
				// 数据解释出错，不能往下继续，直接抛出RuntimeException给顶层捕获
				throw (new RuntimeException("DataKVDao[toT] json转换异常", e));
			}
		}
		return t;
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
}
