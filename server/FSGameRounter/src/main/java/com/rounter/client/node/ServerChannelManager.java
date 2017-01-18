package com.rounter.client.node;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.rounter.client.config.RouterConst;
import com.rounter.innerParam.ReqType;
import com.rounter.innerParam.ReqestObject;
import com.rounter.innerParam.ResponseObject;
import com.rounter.innerParam.jsonParam.AllAreasInfo;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.ResDataFromServer;
import com.rounter.service.IResponseHandler;
import com.rounter.util.JsonUtil;

@Service
public class ServerChannelManager {
	
	private EventLoopGroup senderGroup = new NioEventLoopGroup(RouterConst.MAX_THREAD_COUNT);
	
	private HashMap<String, HashMap<String, ChannelNodeManager>> areaMgrMap2 = new HashMap<String, HashMap<String,ChannelNodeManager>>();
	
	
	private HashMap<String, ChannelNodeManager> areaMgrMap = new HashMap<String, ChannelNodeManager>();	//游戏区连接管理
	private HashMap<String, ChannelNodeManager> platformMgrMap = new HashMap<String, ChannelNodeManager>();	//登录服连接管理
	
	private HashMap<String, ServerInfo> serverMap = new HashMap<String, ServerInfo>();	//区服务器信息
	private HashMap<String, ServerInfo> platformMap = new HashMap<String, ServerInfo>();	//登录服信息
	
	public ServerChannelManager(){
		ServerInfo server = new ServerInfo();
		server.setId("1001");
		server.setIp(RouterConst.TARGET_ADDR);
		server.setPort(RouterConst.TARGET_PORT);
		serverMap.put("1001", server);
		getAreaNodeManager(server);
	}
	
	public synchronized ChannelNodeManager getAreaNodeManager(ServerInfo areaInfo){
		ChannelNodeManager nodeManager = areaMgrMap.get(areaInfo.getId());
		if(null != nodeManager){
			nodeManager.setActiveState(areaInfo.isActive());
			return nodeManager;
		}
		if(areaInfo.isActive()){
			nodeManager = new ChannelNodeManager(senderGroup, areaInfo.getIp(), areaInfo.getPort());
			areaMgrMap.put(areaInfo.getId(), nodeManager);
			if(nodeManager.init()){
				return nodeManager;
			}
		}
		return null;
	}
	
	public ChannelNodeManager getAreaNodeManager(String serverId){
		ServerInfo server = serverMap.get(serverId);
		if(null == server) return null;
		return getAreaNodeManager(server);
	}

	private void refreshPlatformChannel(){
		for(ServerInfo platform : platformMap.values()){
			ChannelNodeManager platformNodeMgr = platformMgrMap.get(platform.getId());
			if(null == platformNodeMgr && platform.isActive()){
				platformNodeMgr = new ChannelNodeManager(senderGroup, platform.getIp(), platform.getPort());
				platformMgrMap.put(platform.getId(), platformNodeMgr);
				platformNodeMgr.init();
			}else{
				platformNodeMgr.setActiveState(platform.isActive());
			}
			if(platform.isActive()){
				refreshServerInfoMap(platformNodeMgr);
			}
		}
	}
	
	private void refreshServerInfoMap(ChannelNodeManager platformNodeMgr){
		final HashMap<String, ServerInfo> serverMap = new HashMap<String, ServerInfo>();
		//platformNodeMgr.sendMessage(reqData, resHandler, resData);
	}
	
	public void getAreasInfo(String platformId) {
		ReqestObject reqObject = new ReqestObject();
		reqObject.setType(ReqType.GetAreaInfo);
		ServerInfo platform = platformMap.get(platformId);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
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
}
