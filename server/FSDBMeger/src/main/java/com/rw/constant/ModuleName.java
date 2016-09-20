package com.rw.constant;

public enum ModuleName {
	START("start"),
	COMMON("common"),
	BACKUP_DB("backup_db"),
	INIT_DBINFO("init_dbinfo"),
	BEFORE_MEGER("before_meger"),
	MEGER("meger"),
	;
	private String name;
	private ModuleName(String _name){
		this.name = _name;
	}
	public String getName() {
		return name;
	}
	
	
}
