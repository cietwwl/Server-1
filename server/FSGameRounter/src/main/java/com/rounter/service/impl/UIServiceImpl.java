package com.rounter.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rounter.client.node.ChannelNodeManager;
import com.rounter.client.node.ServerChannelManager;
import com.rounter.client.node.ServerInfo;
import com.rounter.controller.ucParam.cfg.UCGiftCfg;
import com.rounter.controller.ucParam.cfg.UCGiftCfgDAO;
import com.rounter.innerParam.ReqType;
import com.rounter.innerParam.RouterReqestObject;
import com.rounter.innerParam.RouterRespObject;
import com.rounter.innerParam.ResultState;
import com.rounter.innerParam.jsonParam.AllRolesInfo;
import com.rounter.innerParam.jsonParam.ReqestParams;
import com.rounter.innerParam.jsonParam.UserZoneInfo;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.ResDataFromServer;
import com.rounter.service.IResponseHandler;
import com.rounter.service.IUCService;
import com.rounter.state.UCStateCode;
import com.rounter.util.JsonUtil;

@Service
public class UIServiceImpl implements IUCService{

	@Override
	public IResponseData getRoleInfo(final String platformId, String accountId) {
		RouterReqestObject reqObject = new RouterReqestObject();
		reqObject.setType(ReqType.GetSelfRoles);
		ReqestParams param = new ReqestParams();
		param.setAccountId(accountId);
		reqObject.setContent(JsonUtil.writeValue(param));
		ChannelNodeManager channelMgr = ServerChannelManager.getInstance().getPlatformNodeManager(platformId);
		IResponseData resData = new ResDataFromServer();
		IResponseHandler handler = new IResponseHandler() {
			
			@Override
			public void handleServerResponse(Object msgBack, IResponseData response) {
				RouterRespObject resObject = JsonUtil.readValue((String)msgBack, RouterRespObject.class);
				if(resObject.getResult() == ResultState.SUCCESS){
					AllRolesInfo roles = JsonUtil.readValue((String)resObject.getContent(), AllRolesInfo.class);
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
					response.setStateCode(resObject.getResult().getUCStateCode().getId());
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
	public IResponseData getAreasInfo(String platformId, int page, int count) {
		IResponseData response = new ResDataFromServer();
		List<ServerInfo> servers = ServerChannelManager.getInstance().getAllAreas(platformId);
		if(null != servers){
			JSONObject jsObj = new JSONObject();
			int size = servers.size();
			jsObj.put("recordCount", size);
			JSONArray jsArray = new JSONArray();
			jsObj.put("list", jsArray);
			if(size <= page * count){
				page = 1;
			}
			for(int i = (page -1) * count; i < size && i < page * count; i++){
				ServerInfo info = servers.get(i);
				JSONObject jsServer = new JSONObject();
				jsServer.put("serverId", info.getId());
				jsServer.put("serverName", info.getName());
				jsArray.add(jsServer);
			}
			response.setStateCode(UCStateCode.STATE_OK.getId());
			response.setData(jsObj);
		}else{
			response.setStateCode(UCStateCode.STATE_PARAM_ERROR.getId());
		}
		return response;
	}

	@Override
	public IResponseData getGift(String areaId, String userId, String giftId, String getDate) {
		ChannelNodeManager nodeMgr = ServerChannelManager.getInstance().getAreaNodeManager(areaId);
		IResponseData response = new ResDataFromServer();
		if(null != nodeMgr && nodeMgr.isActive()){
			RouterReqestObject reqObject = new RouterReqestObject();
			reqObject.setType(ReqType.GetGift);
			ReqestParams param = new ReqestParams();
			param.setRoleId(userId);
			param.setGiftId(giftId);
			param.setDate(getDate);
			reqObject.setContent(JsonUtil.writeValue(param));
			IResponseHandler handler = new IResponseHandler() {
				
				@Override
				public void handleServerResponse(Object msgBack, IResponseData response) {
					RouterRespObject resObject = JsonUtil.readValue((String)msgBack, RouterRespObject.class);
					JSONObject jsObj = new JSONObject();
					if(resObject.getResult() == ResultState.SUCCESS){
						response.setStateCode(UCStateCode.STATE_OK.getId());
					}
			
					if(resObject.getResult() == ResultState.SUCCESS){
						jsObj.put("result", true);
					}else{
						jsObj.put("result", false);
					}
					response.setData(jsObj);
				}
				
				@Override
				public void handleSendFailResponse(IResponseData response) {
					response.setStateCode(UCStateCode.STATE_SERVER_ERROR.getId());
				}
				
			};
			try {
				nodeMgr.sendMessage(reqObject, handler, response);
			} catch (Exception e) {
				handler.handleSendFailResponse(response);
				e.printStackTrace();
			}
		}else{
			response.setStateCode(UCStateCode.STATE_SERVER_ERROR.getId());
		}
		return response;
	}

	@Override
	public IResponseData checkGiftId(String giftId) {
		IResponseData response = new ResDataFromServer();
		UCGiftCfg cfg = UCGiftCfgDAO.getInstance().getCfgById(giftId);
		boolean result = cfg == null ? false : true;
		JSONObject jsObj = new JSONObject();
		jsObj.put("result", result);
		response.setData(jsObj);
		response.setStateCode(UCStateCode.STATE_OK.getId());
		return response;
	}
}
