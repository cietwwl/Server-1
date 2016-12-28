package com.rounter.client.node;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rounter.client.config.RouterConst;
import com.rounter.innerParam.ReqType;
import com.rounter.innerParam.RouterReqestObject;
import com.rounter.innerParam.RouterRespObject;
import com.rounter.innerParam.ResultState;
import com.rounter.innerParam.jsonParam.AllAreasInfo;
import com.rounter.innerParam.jsonParam.TableZoneInfo;
import com.rounter.loginServer.LoginServerInfo;
import com.rounter.param.IResponseData;
import com.rounter.param.impl.ResDataFromServer;
import com.rounter.service.IResponseHandler;
import com.rounter.util.JsonUtil;

@Service
public class ServerChannelManager {
	
	Logger logger = LoggerFactory.getLogger(ServerChannelManager.class);
	
	private static ServerChannelManager instance = new ServerChannelManager();
	
	protected ServerChannelManager(){	}
	
	public static ServerChannelManager getInstance(){
		return instance;
	}
	
	private EventLoopGroup senderGroup = new NioEventLoopGroup(RouterConst.MAX_THREAD_COUNT);
	
	//游戏服连接管理(第一个key是登录服id，第二个key是区id)
	private HashMap<String, HashMap<String, ChannelNodeManager>> areaMgrMap = new HashMap<String, HashMap<String,ChannelNodeManager>>();
	//登录服连接管理key=自定义key
	private HashMap<String, ChannelNodeManager> platformMgrMap = new HashMap<String, ChannelNodeManager>();	
	//区服务器信息(第一个key是登录服id，第二个key是区id)
	private HashMap<String, HashMap<String, ServerInfo>> serverMap = new HashMap<String, HashMap<String,ServerInfo>>();
	//登录服信息key=自定义key
	private HashMap<String, ServerInfo> platformMap = new HashMap<String, ServerInfo>();
	
	public ChannelNodeManager getAreaNodeManager(String platformId, String areaId){
		HashMap<String, ChannelNodeManager> platformAreas = areaMgrMap.get(platformId);
		if(null != platformAreas){
			return platformAreas.get(areaId);
		}
		return null;
	}
	
	public ChannelNodeManager getAreaNodeManager(String areaId){
		for(String platformId : areaMgrMap.keySet()){
			ChannelNodeManager nodeMgr = getAreaNodeManager(platformId, areaId);
			if(null != nodeMgr){
				return nodeMgr;
			}
		}
		return null;
	}
	
	public ChannelNodeManager getPlatformNodeManager(String platformId){
		return platformMgrMap.get(platformId);
	}
	
	public ServerInfo getAreaInfo(String platformId, String areaId){
		HashMap<String, ServerInfo> platformAreas = serverMap.get(platformId);
		if(null != platformAreas){
			return platformAreas.get(areaId);
		}
		return null;
	}
	
	/**
	 * 获取某个登录服下的所有区信息
	 * @param platformId
	 * @return
	 */
	public List<ServerInfo> getAllAreas(String platformId){
		HashMap<String, ServerInfo> platformAreas = serverMap.get(platformId);
		if(null != platformAreas){
			ArrayList<ServerInfo> result = new ArrayList<ServerInfo>(platformAreas.values());
			Collections.sort(result);
			return result;
		}
		return null;
	}

	public void refreshPlatformChannel(){
		logger.debug("begin refresh channel ~~");
		HashMap<String, ServerInfo> platforms = LoginServerInfo.getInstance().checkServerProp();
		if(platforms != null){
			dropNotExistPlatform(platforms);
			this.platformMap = platforms;
		}
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
					if(server == null){
						logger.debug("check and found null area info!!");
						continue;
					}
					if(StringUtils.equals(server.getId(), "29")){
						System.out.println();
					}
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
					}else if(null != serverNodeMgr){
						serverNodeMgr.setActiveState(server.isActive());
					}
				}
			}
		}
		
		logger.debug("refresh channel complete~~");
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
						ChannelNodeManager remove = platformAreaMgrs.remove(serverId);
						if(null != remove){
							remove.close();
						}
					}
				}
			}
		}
	}
	
	/**
	 * 向登录服请求区信息
	 * @param platformNodeMgr
	 * @return
	 */
	private HashMap<String, ServerInfo> getAreasInfo(ChannelNodeManager platformNodeMgr) {
		RouterReqestObject reqObject = new RouterReqestObject();
		reqObject.setType(ReqType.GetAreaInfo);	
		final HashMap<String, ServerInfo> platformAreas = new HashMap<String, ServerInfo>();
		IResponseData resData = new ResDataFromServer();
		IResponseHandler handler = new IResponseHandler() {
			
			@Override
			public void handleServerResponse(Object msgBack, IResponseData response) {
				try {
					RouterRespObject resObject = JsonUtil.readValue((String)msgBack, RouterRespObject.class);
					if(resObject.getResult() == ResultState.SUCCESS){
						
						AllAreasInfo areas = JsonUtil.readValue(resObject.getContent(), AllAreasInfo.class);
						if(null != areas && null != areas.getZoneList()){
							for(TableZoneInfo zoneInfo : areas.getZoneList()){
								ServerInfo area = new ServerInfo();
								area.setId(String.valueOf(zoneInfo.getZoneId()));
								area.setName(zoneInfo.getZoneName());
								area.setIp(zoneInfo.getIntranetIp());
								area.setPort(zoneInfo.getUcGiftRounterPort());
								//TODO 判断服务器状态  status -1是关闭
								area.setActive(zoneInfo.getStatus() >= 0);
								platformAreas.put(area.getId(), area);
							}
						}
					}
				} catch (Exception e) {
					handleSendFailResponse(response);
					e.printStackTrace();
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
