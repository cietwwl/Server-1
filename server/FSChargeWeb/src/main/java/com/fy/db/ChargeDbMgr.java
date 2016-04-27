package com.fy.db;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.druid.pool.DruidDataSource;
import com.fy.common.SpringContextUtil;


public class ChargeDbMgr {

	private	RawSqlJdbc rawSqlJdbc;
	
	private Map<Class<?>, ClassInfo> classInfoMap= new ConcurrentHashMap<Class<?>, ClassInfo> ();
	
	public ChargeDbMgr(DruidDataSource dataSource){
		rawSqlJdbc = new RawSqlJdbc(dataSource);		
	}
	
	public static ChargeDbMgr getInstance(){
		return SpringContextUtil.getBean("chargeDbMg");
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
