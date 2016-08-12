package com.playerdata.dataEncode;

public interface IFieldToStr {	
	
	public String toStr (Object target)  throws Exception;
	
	//获取Filed的信息，打log的时候用
	public String getLogInfo();
	
}
