package com.rounter.param;

import com.alibaba.fastjson.JSON;

/**
 * 9游的请求参数
 * @author Alex
 *
 * 2016年12月11日 下午6:00:24
 */
public class Request9Game {
	
	private long id;
	

	private String data;
	
	private String client;
	//加密方式
	private String encrypt;
	
	//签名参数
	private String sign;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	
}
