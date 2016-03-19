package com.dx.gods.common.log;



public enum EnumLog {
	GM_LOG(1,"gm_log", "./logs/gm"),
	GS_LOG(2, "gs_log", "./logs/gs"),
	UPDATERES_LOG(3,"updateres_log", "./logs/updateres")
	;
	private int type;
	private String LogName;
	private String path;
	private EnumLog(int type, String LogName, String path){
		this.type = type;
		this.LogName = LogName;
		this.path = path;
	}
	public int getType() {
		return type;
	}
	public String getLogName() {
		return LogName;
	}

	public String getPath() {
		return path;
	}

	private static EnumLog[] AllValue;
	
	public static EnumLog getEnumLog(int type) {
		if (AllValue == null) {
			AllValue = EnumLog.values();
		}
		for (EnumLog enumLog : AllValue) {
			if (enumLog.getType() == type) {
				return enumLog;
			}
		}
		return null;
	}
}
