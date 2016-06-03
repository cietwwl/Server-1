package com.gm.customer.response;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class QuestionSubmitResponse {
	private long iSequenceNum;
	private int type;
	
	
	public long getiSequenceNum() {
		return iSequenceNum;
	}
	public void setiSequenceNum(long iSequenceNum) {
		this.iSequenceNum = iSequenceNum;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}