package com.rw.service.log.eLog;

public enum eBILogType {
	AccountRegLog(1,"帐号注册", "AccountRegLog"),
	AccountLoginLog(1,"帐号登陆", "AccountLoginLog"),
	ModelRegLog(1,"机型注册", "ModelRegLog")
	;
	
	
	
	private int logId;
	private String logDesc;
	private String logName;
	
	private eBILogType(int _logId, String _logDesc, String _logName){
		this.logId = _logId;
		this.logDesc = _logDesc;
		this.logName = _logName;
	}

	public int getLogId() {
		return logId;
	}
	public String getLogDesc() {
		return logDesc;
	}
	public String getLogName() {
		return logName;
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
