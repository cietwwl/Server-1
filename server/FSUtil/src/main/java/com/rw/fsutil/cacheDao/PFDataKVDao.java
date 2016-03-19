package com.rw.fsutil.cacheDao;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.dao.JdbcDataKVDao;
import com.rw.fsutil.dao.annotation.ClassHelper;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.log.SqlLog;
import com.rw.fsutil.util.jackson.JsonUtil;

/**
 * 前台数据(支持单表和多表)
 * 数据库+memcached
 * @author Ace
 *
 * @param <T>
 * @param <ID>
 */
public class PFDataKVDao<T> 
{

	private JdbcDataKVDao<T> jdbcKVDao;
	
	private ClassInfo classInfoPojo = null;

	public PFDataKVDao() {
		Class<T> clazz = ClassHelper.getEntityClass(this.getClass());
		try {
			classInfoPojo = new ClassInfo(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		jdbcKVDao = new JdbcDataKVDao<T>(clazz, "dataSourcePF");
		
	}

	
	public T get(String id)   {
		if(StringUtils.isBlank(id)){
			return null;
		}

		String value = jdbcKVDao.get(id);
		T t = toT(value);
		
		return t;
	}

	
	public boolean update(T t){
		
		boolean success=  false;
		String id = getId(t);
		if(StringUtils.isBlank(id)){
			return false;
		}
		
		String writeValue = JsonUtil.writeValue(t);

		if (id!=null && writeValue!=null ) {
			success = jdbcKVDao.saveOrUpdate(id, writeValue);			
		}
		return success;
	}
	

	private String getId(T t){
		Field idField = classInfoPojo.getIdField();
		
		String id = null;
		try {
			id = (String)idField.get(t);
		} catch (Exception e) {
			SqlLog.error(e);
		} 
		return  id;
	};



	public boolean delete(String id)   {
		if(StringUtils.isBlank(id)){
			return false;
		}
	
		try {
			jdbcKVDao.delete(id);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			SqlLog.error(e);
		}
			
	
		return false;
	}		


	private T toT(String value) {
		T t = null;
		if(StringUtils.isNotBlank(value)){
			t = (T) JsonUtil.readValue(value, getEntityClass());
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getEntityClass() {
		return (Class<T>) classInfoPojo.getClazz();
	}

}
