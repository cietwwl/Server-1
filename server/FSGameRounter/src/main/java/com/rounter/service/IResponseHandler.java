package com.rounter.service;

import com.rounter.param.IResponseData;

public interface IResponseHandler {
	
	/**
	 * 处理请求返回的信息
	 * <li>主要是把msgBack中的数据，转化到response中</li>
	 * @param msgBack 服务端返回数据
	 * @param response 转化的目标数据
	 */
	public void handleServerResponse(Object msgBack, IResponseData response);
	
	/**
	 * 发送失败的处理
	 * @param response 要将失败结果写入这个response中
	 */
	public void handleSendFailResponse(IResponseData response);
	
}
