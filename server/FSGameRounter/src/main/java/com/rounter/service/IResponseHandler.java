package com.rounter.service;

import com.rounter.param.IResponseData;

public interface IResponseHandler {
	
	public void handleServerResponse(Object msgBack, IResponseData response);
	
	public void handleSendFailResponse(IResponseData response);
	
}
