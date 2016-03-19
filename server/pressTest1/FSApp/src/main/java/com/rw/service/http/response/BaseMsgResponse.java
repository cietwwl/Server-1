package com.rw.service.http.response;

import java.io.Serializable;

public class BaseMsgResponse implements Serializable{
	private static final long serialVersionUID = -5182532647273100000L;
	private String msg;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
