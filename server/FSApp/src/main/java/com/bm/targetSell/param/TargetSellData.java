package com.bm.targetSell.param;

import com.alibaba.fastjson.JSONObject;
import com.bm.targetSell.TargetSellManager;


public class TargetSellData {

	private int opType;
	
	private String sign;
	
	private JSONObject args;

	
	private TargetSellData() {
	}

	public static TargetSellData create(int opType){
		TargetSellData data = new TargetSellData();
		data.opType = opType;
		data.sign = TargetSellManager.MD5_Str;
		return data;
	}
	
	
	
	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}

	public String getSign(){
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public JSONObject getArgs() {
		return args;
	}

	public void setArgs(JSONObject args) {
		this.args = args;
	}

	
	
}
