package com.rounter.param;

import com.alibaba.fastjson.JSON;


/**
 * 9游的响应参数
 * @author Alex
 *
 * 2016年12月11日 下午6:07:17
 */
public class Response9Game {

	private long id;
	private JSON state;
	private JSON data;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public JSON getState() {
		return state;
	}
	public void setState(JSON state) {
		this.state = state;
	}
	public JSON getData() {
		return data;
	}
	public void setData(JSON data) {
		this.data = data;
	}
	
	
	
	
}
