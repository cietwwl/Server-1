package com.rounter.innerParam;

import com.rounter.param.IRequestData;
import com.rounter.param.ReqType;

public class ReqestObject implements IRequestData{
	
	private ReqType type;
	
	private String content;
	
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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
