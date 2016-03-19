package com.rw.fsutil.dao.common;

import java.util.List;

class CommonSqlContext {

	private String primaryKey;
	
	private String sql;
	
	private List<Object> params;

	public static CommonSqlContext build(String sql, String primaryKey, List<Object> params){
		CommonSqlContext sqlContext = new CommonSqlContext();
		sqlContext.setSql(sql).setPrimaryKey(primaryKey).setParams(params);
		return sqlContext;
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}

	public CommonSqlContext setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
		return this;
	}

	public String getSql() {
		return sql;
	}

	public CommonSqlContext setSql(String sql) {
		this.sql = sql;
		return this;
	}

	public List<Object> getParams() {
		return params;
	}

	public CommonSqlContext setParams(List<Object> params) {
		this.params = params;
		return this;
	}
	
	
	
}
