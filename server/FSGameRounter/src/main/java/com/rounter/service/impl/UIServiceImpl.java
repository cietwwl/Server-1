package com.rounter.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rounter.client.node.ChannelNodeManager;
import com.rounter.client.node.ServerChannelManager;
import com.rounter.innerParam.ReqType;
import com.rounter.innerParam.ReqestObject;
import com.rounter.param.IRequestData;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.ResDataFromServer;
import com.rounter.service.IResponseHandler;
import com.rounter.service.IUCService;
import com.rounter.util.JsonUtil;

@Service
public class UIServiceImpl implements IUCService{

	@Autowired
	private ServerChannelManager serverMgr;
	
	@Override
	public IResponseData getRoleInfo(IRequestData request) {
		ReqestObject reqObject = new ReqestObject();
		reqObject.setType(ReqType.GetSelfRoles);
		reqObject.setContent(JsonUtil.writeValue(request));
		ChannelNodeManager channelMgr = serverMgr.getAreaNodeManager(request.requestId());
		IResponseData resData = new ResDataFromServer();
		IResponseHandler handler = new IResponseHandler() {
			
			@Override
			public void handleServerResponse(Object msgBack, IResponseData response) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void handleSendFailResponse(IResponseData response) {
				// TODO Auto-generated method stub
				
			}
		};
		if(null != channelMgr){
			try {
				channelMgr.sendMessage(reqObject, handler, resData);
			} catch (Exception e) {
				handler.handleSendFailResponse(resData);
				e.printStackTrace();
			}
		}else{
			handler.handleSendFailResponse(resData);
		}
		
		return resData;
	}
}
