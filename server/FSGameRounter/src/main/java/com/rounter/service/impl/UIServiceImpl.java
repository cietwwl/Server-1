package com.rounter.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rounter.client.node.ChannelNodeManager;
import com.rounter.client.node.ServerChannelManager;
import com.rounter.controller.ucParam.ReqRoleInfo;
import com.rounter.innerParam.ReqType;
import com.rounter.innerParam.ReqestObject;
import com.rounter.innerParam.ResponseObject;
import com.rounter.innerParam.jsonParam.AllAreasInfo;
import com.rounter.innerParam.jsonParam.ReqestParams;
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
		ReqestParams param = new ReqestParams();
		param.setAccountId(((ReqRoleInfo)request).getAccountId());
		reqObject.setContent(JsonUtil.writeValue(param));
		ChannelNodeManager channelMgr = serverMgr.getAreaNodeManager(request.requestId());
		IResponseData resData = new ResDataFromServer();
		IResponseHandler handler = new IResponseHandler() {
			
			@Override
			public void handleServerResponse(Object msgBack, IResponseData response) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@:" + msgBack);
			}
			
			@Override
			public void handleSendFailResponse(IResponseData response) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@: send fail");
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
	
	@Override
	public IResponseData getAreasInfo(String platformId) {
		ReqestObject reqObject = new ReqestObject();
		reqObject.setType(ReqType.GetAreaInfo);
		ChannelNodeManager channelMgr = serverMgr.getAreaNodeManager(platformId);
		IResponseData resData = new ResDataFromServer();
		IResponseHandler handler = new IResponseHandler() {
			
			@Override
			public void handleServerResponse(Object msgBack, IResponseData response) {
				ResponseObject resObject = JsonUtil.readValue((String)msgBack, ResponseObject.class);
				if(resObject.isSuccess()){
					AllAreasInfo areas = JsonUtil.readValue(resObject.getResult(), AllAreasInfo.class);
					System.out.println("areas size: " + areas.getZoneList().size());
				}
			}
			
			@Override
			public void handleSendFailResponse(IResponseData response) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@: send fail");
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
