package com.rounter.param.impl;

import com.alibaba.fastjson.JSONObject;
import com.rounter.param.IResponseData;
import com.rounter.state.UCStateCode;

public class ResDataFromServer implements IResponseData{
	
	private JSONObject data;
	
	private int stateCode;
	
	/**
	 * 初始化的时候默认值
	 * @param stateCode
	 */
	public ResDataFromServer(int stateCode) {
		this.stateCode = stateCode;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
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
