package com.rounter.client.node;

import com.rounter.param.IResponseData;
import com.rounter.service.IResponseHandler;

public class NodeData {
	
	private final IResponseData resData;
	
	private final IResponseHandler resHandler;

	public NodeData(IResponseData resData, IResponseHandler resHandler){
		this.resData = resData;
		this.resHandler = resHandler;
	}
	
	public IResponseData getResData() {
		return resData;
	}

	public IResponseHandler getResHandler() {
		return resHandler;
	}
}
