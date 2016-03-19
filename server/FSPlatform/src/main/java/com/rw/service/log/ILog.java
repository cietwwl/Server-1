package com.rw.service.log;

import com.rw.service.log.infoPojo.ClientInfo;

public interface ILog {
	
	public void parseLog(String value);
	
	public void setLogValue(Object... values);
	
	public String logToString(ClientInfo clientInfo);
	
	public void fillInfoToClientInfo(ClientInfo clientInfo);
}
