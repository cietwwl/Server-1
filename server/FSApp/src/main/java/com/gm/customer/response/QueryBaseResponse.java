package com.gm.customer.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryBaseResponse {
	private int count;
	private String msg;
	private int status;
	private String result;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
