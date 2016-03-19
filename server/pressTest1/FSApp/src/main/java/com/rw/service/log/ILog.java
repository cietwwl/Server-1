package com.rw.service.log;

public interface ILog {
	public void parseLog(String value);
	
	public void setLogValue(Object... values);
	
	public String logToString();
}
