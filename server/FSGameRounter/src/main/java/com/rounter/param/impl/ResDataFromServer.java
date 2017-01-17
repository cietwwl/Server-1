package com.rounter.param.impl;

import com.alibaba.fastjson.JSONObject;
import com.rounter.param.IResponseData;

public class ResDataFromServer implements IResponseData{
	
	private long id;
	
	private JSONObject data;
	
	private int stateCode;
	
	public void setId(long id) {
		this.id = id;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public int getStateCode() {
		return stateCode;
	}
}
