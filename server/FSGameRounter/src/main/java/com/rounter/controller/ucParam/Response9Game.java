package com.rounter.controller.ucParam;

/**
 * 9游的响应参数
 * @author Alex
 *
 * 2016年12月11日 下午6:07:17
 */
public class Response9Game{

	private long id;
	private Object state;
	private Object data;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Object getState() {
		return state;
	}
	
	public void setState(Object state) {
		this.state = state;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
}
