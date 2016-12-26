package com.rounter.param;

import com.alibaba.fastjson.JSONObject;

public interface IResponseData {
	
	/**
	 * 获取返回数据的json字符串
	 * @return
	 */
	public JSONObject getData();
	
	public void setData(JSONObject jsObj);
	
	
	/**
	 * 获取状态码
	 * @return
	 */
	public int getStateCode();
	
	public void setStateCode(int state);
}
