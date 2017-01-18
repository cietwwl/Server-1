package com.rounter.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rounter.client.node.ChannelNodeManager;
import com.rounter.client.node.ServerChannelManager;
import com.rounter.client.node.ServerInfo;
import com.rounter.innerParam.ReqType;
import com.rounter.innerParam.ReqestObject;
import com.rounter.innerParam.ResponseObject;
import com.rounter.innerParam.jsonParam.AllRolesInfo;
import com.rounter.innerParam.jsonParam.ReqestParams;
import com.rounter.innerParam.jsonParam.UserZoneInfo;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.ResDataFromServer;
import com.rounter.service.IResponseHandler;
import com.rounter.state.UCStateCode;
import com.rounter.util.JsonUtil;

@Service
public class UIServiceImpl implements IUCService{

	@Override
	public IResponseData getRoleInfo(final String platformId, String accountId) {
		ReqestObject reqObject = new ReqestObject();
		reqObject.setType(ReqType.GetSelfRoles);
		ReqestParams param = new ReqestParams();
		param.setAccountId(accountId);
		reqObject.setContent(JsonUtil.writeValue(param));
		ChannelNodeManager channelMgr = ServerChannelManager.getInstance().getPlatformNodeManager(platformId);
		IResponseData resData = new ResDataFromServer();
		IResponseHandler handler = new IResponseHandler() {
			
			@Override
			public void handleServerResponse(Object msgBack, IResponseData response) {
				ResponseObject resObject = JsonUtil.readValue((String)msgBack, ResponseObject.class);
				if(resObject.isSuccess()){
					AllRolesInfo roles = JsonUtil.readValue((String)msgBack, AllRolesInfo.class);
					JSONObject jsObj = new JSONObject();
					if(null != roles){
						jsObj.put("accountId", roles.getAccountId());
						JSONArray jsArray = new JSONArray();
						jsObj.put("roleInfos", jsArray);
						if(null != roles.getRoles()){
							for(UserZoneInfo zoneInfo : roles.getRoles()){
								JSONObject jsRole = new JSONObject();
								jsRole.put("serverId", zoneInfo.getZoneId());
								ServerInfo serverInfo = ServerChannelManager.getInstance().getAreaInfo(platformId, String.valueOf(zoneInfo.getZoneId()));
								if(null != serverInfo){
									jsRole.put("serverName", serverInfo.getName());
								}else{
									jsRole.put("serverName", "");
								}
								jsRole.put("roleId", zoneInfo.getUserId());
								jsRole.put("roleName", zoneInfo.getUserName());
								jsRole.put("roleLevel", zoneInfo.getLevel());
								jsArray.add(jsRole);
							}
						}
					}
					response.setStateCode(UCStateCode.STATE_OK.getId());
					response.setData(jsObj);
				}else{
					response.setStateCode(UCStateCode.STATE_PARAM_ERROR.getId());
				}
			}
			
			@Override
			public void handleSendFailResponse(IResponseData response) {
				response.setStateCode(UCStateCode.STATE_SERVER_ERROR.getId());
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
		IResponseData resData = new ResDataFromServer();
		List<ServerInfo> servers = ServerChannelManager.getInstance().getAllAreas(platformId);
		if(null == servers){
			resData.setStateCode(UCStateCode.STATE_OK.getId());
		}else{
			resData.setStateCode(UCStateCode.STATE_PARAM_ERROR.getId());
		}
		return resData;
	}

	@Override
	public IResponseData getGift(String platformId, String userId, String giftId) {
		return null;
	}
}
