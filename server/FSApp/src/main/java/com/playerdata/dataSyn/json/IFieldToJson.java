package com.playerdata.dataSyn.json;

public interface IFieldToJson {
	
	public Object toJson (Object target, JsonOpt jsonOpt)  throws Exception;
	
	public void fromJson(Object target, String json) throws Exception;
	
	//获取Filed的信息，打log的时候用
	public String getLogInfo();
	
	
}
