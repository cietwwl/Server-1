package com.rw.service.log.db;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.fsutil.dao.annotation.ClassInfo;
import com.rw.fsutil.dao.common.RawSqlJdbc;

public class BILogDbMgr {

	private	RawSqlJdbc rawSqlJdbc;
	
	private Map<Class<?>, ClassInfo> classInfoMap= new ConcurrentHashMap<Class<?>, ClassInfo> ();
	
	public BILogDbMgr(DruidDataSource dataSource){
		rawSqlJdbc = new RawSqlJdbc(dataSource);
		
	}
	
	public <T> List<T> query(String sql, Object[] args, Class<T> clazz){
		
		
		ClassInfo classInfoPojo = classInfoMap.get(clazz);
		if(classInfoPojo == null){
			classInfoPojo = new ClassInfo(clazz);
			classInfoMap.put(clazz, classInfoPojo);
		}
		
		return rawSqlJdbc.findBySql(sql, args, classInfoPojo, clazz);
	}
	
	
}
