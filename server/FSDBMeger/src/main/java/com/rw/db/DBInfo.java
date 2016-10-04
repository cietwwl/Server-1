package com.rw.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBInfo {
	private String dbName;
	private String username;
	private String password;
	private String ip;
	private int port;
	private String dataSourceName;
	private int zoneId;
	private String importSQL;
	String url;
	
	private Map<String, TableInfo> TableMap = new HashMap<String, TableInfo>();
	

	public String getDBName() {
		return dbName;
	}

	public int getPort() {
		return port;
	}

	public TableInfo getTableInfo(String tableName) {
		return TableMap.get(tableName);
	}

	public void addTable(TableInfo tableInfo){
		TableMap.put(tableInfo.getTableName(), tableInfo);
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	public List<TableInfo> getTables(){
		List<TableInfo> list = new ArrayList<TableInfo>();
		list.addAll(TableMap.values());
		return list;
	}
	
	public void genImportSQL(){
		importSQL = "mysql -uroot -p"+this.password+" -h"+this.ip+" -P"+this.port +" "+this.dbName + " < ";
	}

	public String getImportSQL() {
		return importSQL;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
