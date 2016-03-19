package com.gm;

import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.util.jackson.JsonUtil;

public class GmRequest {

	private int opType;
	private String account;
	private String password;
	private Map<String, Object> args;
	public int getOpType() {
		return opType;
	}
	public void setOpType(int opType) {
		this.opType = opType;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public Map<String, Object> getArgs() {
		return args;
	}
	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}
	public static void main(String[] args) {
		GmRequest gmRequest = new GmRequest();
		gmRequest.setAccount("gm");
		gmRequest.setPassword("123456");
		gmRequest.setOpType(1001);
		Map<String, Object> argsTmp = new HashMap<String, Object>();
		argsTmp.put("value", 0);
		gmRequest.setArgs(argsTmp);
		
		System.out.println(JsonUtil.writeValue(gmRequest));
	}
	
	
}
