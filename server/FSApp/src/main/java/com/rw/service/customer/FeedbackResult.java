package com.rw.service.customer;

import com.rwproto.QuestionServiceProtos.eSubmitResultType;

public class FeedbackResult {
	private eSubmitResultType resultType;
	private String result;
	
	public FeedbackResult(eSubmitResultType resultType, String result){
		this.resultType = resultType;
		this.result = result;
	}
	
	public FeedbackResult(){}
	
	public eSubmitResultType getResultType() {
		return resultType;
	}
	public void setResultType(eSubmitResultType resultType) {
		this.resultType = resultType;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	
}
