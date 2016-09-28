package com.rw.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rw.dblog.DBLog;

public class TableInfo {

	Map<String, Class<?>> FieldsMap = new HashMap<String, Class<?>>();
	private String autoIncFieldName;
	private String keyName;
	private String tableName;
	private String insertSQL;
	private String updateSQL;
	private List<String> FieldOrderList = new LinkedList<String>();

	public void genInsertSQL() {
		Set<String> keySet = FieldsMap.keySet();
		StringBuilder sb = new StringBuilder();
		sb.append("insert into `").append(tableName).append("` (");
		int count = 0;
		int contentCount = 0;
		for (String fieldName : keySet) {
			count++;
			if (fieldName.equals(autoIncFieldName)) {
				continue;
			}
			FieldOrderList.add(fieldName);
			sb.append("`").append(fieldName);
			contentCount++;
			if (count < keySet.size()) {
				sb.append("`,");
			} else {
				sb.append("`)");
			}
		}
		if (contentCount == 0) {
			return;
		}
		sb.append(" values ");
		insertSQL = sb.toString();
	}

	public void genUpdateSQL() {
		Set<String> keySet = FieldsMap.keySet();
		StringBuilder sb = new StringBuilder();
		sb.append("update ").append(tableName).append(" set (");
		int count = 0;
		int contentCount = 0;
		for (String fieldName : keySet) {
			count++;
			if (fieldName.equals(autoIncFieldName)) {
				continue;
			}
			if (fieldName.equals(keyName)) {
				continue;
			}
			sb.append(fieldName).append("= ?");
			contentCount++;

			if (count < keySet.size()) {
				sb.append(",");
			} else {
				sb.append(")");
			}
		}

		if (contentCount == 0) {
			return;
		}
		sb.append(" where ").append(keyName).append("= ?");
		updateSQL = sb.toString();
	}

	public String getInsertSQL() {
		return insertSQL;
	}

	public void setFieldsMap(Map<String, Class<?>> fieldsMap) {
		FieldsMap = fieldsMap;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getAutoIncFieldName() {
		return autoIncFieldName;
	}

	public void setAutoIncFieldName(String autoIncFieldName) {
		this.autoIncFieldName = autoIncFieldName;
	}

	public Map<String, Class<?>> getFieldsMap() {
		return FieldsMap;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public List<String> getFieldOrderList() {
		return FieldOrderList;
	}
}
