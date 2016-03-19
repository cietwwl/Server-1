package com.playerdata.dataSyn.json;

public interface IFieldToJson {
	
	public String toJson (Object target)  throws Exception;
	
	//获取Filed的信息，打log的时候用
	public String getLogInfo();
	
	
}
