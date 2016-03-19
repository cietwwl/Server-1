package com.rw.service.log.eLog;

public enum eLogType {
	RegLog(1,"机型注册");
	
	private int logId;
	private String logDesc;
	
	private eLogType(int _logId, String _logDesc){
		this.logId = _logId;
		this.logDesc = _logDesc;
	}

	public int getLogId() {
		return logId;
	}
	public String getLogDesc() {
		return logDesc;
	}
	
	private static eLogType[] allValue;
	
	public static eLogType getLogType(int type){
		if(allValue == null){
			allValue = eLogType.values();
		}
		
		for (eLogType logType : allValue) {
			if(logType.getLogId() == type){
				return logType;
			}
		}
		return RegLog;
	}
}
