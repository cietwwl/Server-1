package com.rw.db.process.generate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;
import com.rw.db.DBInfo;
import com.rw.db.TableInfo;
import com.rw.db.dao.DBMgr;
import com.rw.db.dao.JdbcTemplateFactory;
import com.rw.dblog.DBLog;
import com.rw.utils.DBTypeDictionary;
import com.rw.utils.SpringContextUtil;

public class GenDBInfoProcesser {

	JdbcTemplate jdbc;
	DBInfo dbInfo;
	List<String> tableNameList = new ArrayList<String>();

	public GenDBInfoProcesser() {
	}

	public void GenDBInfo(DBInfo dbInfo) {
		this.dbInfo = dbInfo;
		achieveAllTable();

	}

	private void achieveAllTable() {

		String sql = "select table_name as tableName from information_schema.tables where table_schema='" + this.dbInfo.getDBName() + "' and table_type='base table';";
		List<tableNameInfo> query = DBMgr.getInstance().query(dbInfo.getDBName(), sql, new Object[] {}, tableNameInfo.class);
		
		for (tableNameInfo tableNameInfo : query) {
			queryTableFiedldInfo(tableNameInfo.getTableName());
		}
		
		this.dbInfo.genImportSQL();
	}
	
	private void queryTableFiedldInfo(String tableName){
		String sql = "SELECT COLUMN_NAME as fieldName, DATA_TYPE as dataType, EXTRA as extra, COLUMN_KEY as columnKey FROM `information_schema`.`COLUMNS` where `TABLE_NAME`='"+tableName+"' and `TABLE_SCHEMA`='"+this.dbInfo.getDBName()+"'";
		
		List<tableFieldInfo> query = DBMgr.getInstance().query(dbInfo.getDBName(), sql, new Object[] {}, tableFieldInfo.class);

		TableInfo tableInfo = new TableInfo();
		Map<String, Class<?>> fieldsMap = new HashMap<String, Class<?>>();
		for (tableFieldInfo tableNameInfo : query) {
			String fieldName = tableNameInfo.getFieldName();
			Class<?> type = DBTypeDictionary.MySQLTypeDictionary.get(tableNameInfo.getDataType().toUpperCase());
			fieldsMap.put(fieldName, type);
			if(tableNameInfo.getExtra().equals("auto_increment")){
				tableInfo.setAutoIncFieldName(fieldName);
			}
			if(tableNameInfo.getColumnKey().equals("PRI")){
				tableInfo.setKeyName(tableNameInfo.getFieldName());
			}
		}
		if (tableInfo.getKeyName() == null) {
			DBLog.LogError("gensql", "---------------------------------------gen sql " + tableName + " not have key");
		}
		tableInfo.setFieldsMap(fieldsMap);
		tableInfo.setTableName(tableName);
		tableInfo.genInsertSQL();
		dbInfo.addTable(tableInfo);
	}
}
