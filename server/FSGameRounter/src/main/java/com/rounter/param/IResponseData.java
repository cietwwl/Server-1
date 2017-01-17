package com.rounter.param;

import com.alibaba.fastjson.JSONObject;

public interface IResponseData {
	
	public long getId();
	
	/**
	 * 获取返回数据的json字符串
	 * @return
	 */
	public JSONObject getData();
	
	
	/**
	 * 获取状态码
	 * @return
	 */
	public int getStateCode();
}
