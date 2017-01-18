package com.rounter.client.node;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.rounter.client.config.RouterConst;
import com.rounter.innerParam.ReqType;
import com.rounter.innerParam.ReqestObject;
import com.rounter.innerParam.ResponseObject;
import com.rounter.innerParam.jsonParam.AllAreasInfo;
import com.rounter.innerParam.jsonParam.TableZoneInfo;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.ResDataFromServer;
import com.rounter.service.IResponseHandler;
import com.rounter.util.JsonUtil;

@Service
public class ServerChannelManager {
	
	private static ServerChannelManager instance = new ServerChannelManager();
	
	protected ServerChannelManager(){	}
	
	public static ServerChannelManager getInstance(){
		return instance;
	}
	
	private EventLoopGroup senderGroup = new NioEventLoopGroup(RouterConst.MAX_THREAD_COUNT);
	
	//游戏服连接管理(第一个key是登录服id，第二个key是区id)
	private HashMap<String, HashMap<String, ChannelNodeManager>> areaMgrMap = new HashMap<String, HashMap<String,ChannelNodeManager>>();
	//登录服连接管理
	private HashMap<String, ChannelNodeManager> platformMgrMap = new HashMap<String, ChannelNodeManager>();	
	//区服务器信息(第一个key是登录服id，第二个key是区id)
	private HashMap<String, HashMap<String, ServerInfo>> serverMap = new HashMap<String, HashMap<String,ServerInfo>>();
	//登录服信息
	private HashMap<String, ServerInfo> platformMap = new HashMap<String, ServerInfo>();	
	
	public ChannelNodeManager getAreaNodeManager(String platformId, String areaId){
		HashMap<String, ChannelNodeManager> platformAreas = areaMgrMap.get(platformId);
		if(null != platformAreas){
			return platformAreas.get(areaId);
		}
		return null;
	}
	
	public ChannelNodeManager getPlatformNodeManager(String platformId){
		return platformMgrMap.get(platformId);
	}

	public void refreshPlatformChannel(){
		HashMap<String, ServerInfo> platforms = refreshPlatformMap();
		dropNotExistPlatform(platforms);
		this.platformMap = platforms;
		for(ServerInfo platform : this.platformMap.values()){
			ChannelNodeManager platformNodeMgr = platformMgrMap.get(platform.getId());
			if(null == platformNodeMgr && platform.isActive()){
				platformNodeMgr = new ChannelNodeManager(senderGroup, platform.getIp(), platform.getPort());
				platformMgrMap.put(platform.getId(), platformNodeMgr);
				platformNodeMgr.init();
			}else{
				platformNodeMgr.setActiveState(platform.isActive());
			}
			if(platform.isActive()){
				HashMap<String, ServerInfo> platformAreas = getAreasInfo(platformNodeMgr);
				dropNotExistServer(platform.getId(), platformAreas);
				serverMap.put(platform.getId(), platformAreas);
				for(ServerInfo server : platformAreas.values()){
					HashMap<String, ChannelNodeManager> platformAreaMgrs = areaMgrMap.get(platform.getId());
					if(null == platformAreaMgrs){
						platformAreaMgrs = new HashMap<String, ChannelNodeManager>();
						areaMgrMap.put(platform.getId(), platformAreaMgrs);
					}
					ChannelNodeManager serverNodeMgr = platformAreaMgrs.get(server.getId());
					if(null == serverNodeMgr && server.isActive()){
						serverNodeMgr = new ChannelNodeManager(senderGroup, server.getIp(), server.getPort());
						platformAreaMgrs.put(server.getId(), serverNodeMgr);
						serverNodeMgr.init();
					}else{
						serverNodeMgr.setActiveState(server.isActive());
					}
				}
			}
		}
	}
	
	/**
	 * 删除掉不存在的登录服连接
	 * @param platforms 当前的登录服
	 */
	private void dropNotExistPlatform(HashMap<String, ServerInfo> platforms){
		Iterator<Entry<String, ServerInfo>> itor = platformMap.entrySet().iterator();
		while(itor.hasNext()){
			String platformId = itor.next().getKey();
			if(!platforms.containsKey(platformId)){
				itor.remove();
				ChannelNodeManager platformMgr = platformMgrMap.get(platformId);
				if(null != platformMgr){
					platformMgr.close();
					platformMgrMap.remove(platformId);
				}
				HashMap<String, ChannelNodeManager> nodeMap = areaMgrMap.get(platformId);	//平台下的所有区
				if(null != nodeMap){
					for(ChannelNodeManager areaMgr : nodeMap.values()){
						areaMgr.close();
					}
					nodeMap.remove(platformId);
				}
			}
		}
	}
	
	/**
	 * 删除掉不存在的区信息
	 * @param platformId
	 * @param servers
	 */
	private void dropNotExistServer(String platformId, HashMap<String, ServerInfo> servers){
		HashMap<String, ServerInfo> platformServers = serverMap.get(platformId);
		if(null != platformServers){
			Iterator<Entry<String, ServerInfo>> itor = platformServers.entrySet().iterator();
			while(itor.hasNext()){
				String serverId = itor.next().getKey();
				if(!servers.containsKey(serverId)){
					itor.remove();
					HashMap<String, ChannelNodeManager> platformAreaMgrs = areaMgrMap.get(platformId);
					if(null != platformAreaMgrs){
						ChannelNodeManager areaMgr = platformAreaMgrs.get(serverId);
						if(null != areaMgr){
							areaMgr.close();
							platformAreaMgrs.remove(serverId);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 刷新登录服信息
	 * @return
	 */
	private HashMap<String, ServerInfo> refreshPlatformMap(){
		HashMap<String, ServerInfo> result = new HashMap<String, ServerInfo>();
		ServerInfo platform = new ServerInfo();
		platform.setId("1001");
		platform.setIp(RouterConst.TARGET_ADDR);
		platform.setPort(RouterConst.TARGET_PORT);
		result.put("1001", platform);
		return result;
	}
	
	/**
	 * 向登录服请求区信息
	 * @param platformNodeMgr
	 * @return
	 */
	private HashMap<String, ServerInfo> getAreasInfo(ChannelNodeManager platformNodeMgr) {
		ReqestObject reqObject = new ReqestObject();
		reqObject.setType(ReqType.GetAreaInfo);	
		final HashMap<String, ServerInfo> platformAreas = new HashMap<String, ServerInfo>();
		IResponseData resData = new ResDataFromServer();
		IResponseHandler handler = new IResponseHandler() {
			
			@Override
			public void handleServerResponse(Object msgBack, IResponseData response) {
				ResponseObject resObject = JsonUtil.readValue((String)msgBack, ResponseObject.class);
				if(resObject.isSuccess()){
					
					AllAreasInfo areas = JsonUtil.readValue(resObject.getResult(), AllAreasInfo.class);
					if(null != areas && null != areas.getZoneList()){
						for(TableZoneInfo zoneInfo : areas.getZoneList()){
							ServerInfo area = new ServerInfo();
							area.setId(String.valueOf(zoneInfo.getZoneId()));
							area.setName(zoneInfo.getZoneName());
							area.setIp(zoneInfo.getServerIp());
							area.setPort(Integer.valueOf(zoneInfo.getPort()));
							//TODO 判断服务器状态
							area.setActive(true);
							platformAreas.put(area.getId(), area);
						}
					}

				}
			}
			
			@Override
			public void handleSendFailResponse(IResponseData response) {
				System.out.println("getAreasInfo fail...");
			}
			
		};
		if(null != platformNodeMgr){
			try {
				platformNodeMgr.sendMessage(reqObject, handler, resData);
			} catch (Exception e) {
				handler.handleSendFailResponse(resData);
				e.printStackTrace();
			}
		}else{
			handler.handleSendFailResponse(resData);
		}
		return platformAreas;
	}
}
