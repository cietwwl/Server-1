package com.rw.service.log.eLog;

public enum eBILogType {
	AccountRegLog(1,"帐号注册"),
	AccountLoginLog(1,"帐号登陆"),
	ModelRegLog(1,"机型注册")
	;
	
	
	
	private int logId;
	private String logDesc;
	
	private eBILogType(int _logId, String _logDesc){
		this.logId = _logId;
		this.logDesc = _logDesc;
	}

	public int getLogId() {
		return logId;
	}
	public String getLogDesc() {
		return logDesc;
	}
	
	private static eBILogType[] allValue;
	
	public static eBILogType getLogType(int type){
		if(allValue == null){
			allValue = eBILogType.values();
		}
		
		for (eBILogType logType : allValue) {
			if(logType.getLogId() == type){
				return logType;
			}
		}
		return null;
	}
}
