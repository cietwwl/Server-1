package com.rw.fsutil.cacheDao.loader;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.cache.DataNotExistException;
import com.rw.fsutil.dao.cache.DuplicatedKeyException;
import com.rw.fsutil.dao.cache.PersistentLoader;

/**
 * 把原来代码拷贝过来，还没整理
 * 
 * @param <T>
 */
public class DataKVSactter<T> implements PersistentLoader<String, T> {

	private final ClassInfo classInfoPojo;
	private final JdbcTemplate template;

	public DataKVSactter(ClassInfo classInfoPojo, JdbcTemplate template) {
		this.classInfoPojo = classInfoPojo;
		this.template = template;
	}

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
		if (StringUtils.isBlank(key)) {
			return false;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(classInfoPojo.getTableName()).append(" (dbkey, dbvalue) values(?,?)");
		// this.template.update(sql.toString(),new Object[]{key,
		// HexUtil.bytes2HexStr(value.getBytes("UTF-8"))});
		String writeValue = toJson(value);
		int affectedRows = template.update(sql.toString(), new Object[] { key, writeValue });
		// lida 2015-09-23 执行成功返回的结果是2
		return affectedRows > 0;
	}

	@Override
	public boolean updateToDB(String key, T value) {
		String sql = "update " + classInfoPojo.getTableName() + " set dbvalue = ? where dbkey = ?";
		String writeValue = toJson(value);
		try {
			if (classInfoPojo.getTableName().equals("drop_record")) {
				System.out.println(new Date() + ",drop_trace:" + key + "," + writeValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		int affectedRows = template.update(sql.toString(), new Object[] { writeValue, key });
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
