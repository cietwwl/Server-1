package com.rw.routerServer.data;


public class ReqestObject{
	
	private ReqType type;
	
	private String content;

	public ReqType getType() {
		return type;
	}

	public void setType(ReqType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
