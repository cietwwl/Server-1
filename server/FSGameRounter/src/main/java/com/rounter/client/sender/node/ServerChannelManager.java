package com.rounter.client.sender.node;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.rounter.client.sender.config.RouterConst;


@Service
public class ServerChannelManager {
	
	private EventLoopGroup senderGroup = new NioEventLoopGroup(RouterConst.MAX_THREAD_COUNT);
	private HashMap<String, ChannelNodeManager> mgrMap = new HashMap<String, ChannelNodeManager>();	//游戏区数据
	private HashMap<String, ServerInfo> serverMap = new HashMap<String, ServerInfo>();	//区服务器数据
	
	public ServerChannelManager(){
		ServerInfo server = new ServerInfo();
		server.setId("1001");
		server.setIp(RouterConst.TARGET_ADDR);
		server.setPort(RouterConst.TARGET_PORT);
		serverMap.put("1001", server);
		getAreaNodeManager(server);
	}
	
	public synchronized ChannelNodeManager getAreaNodeManager(ServerInfo areaInfo){
		ChannelNodeManager nodeManager = mgrMap.get(areaInfo.getId());
		if(null != nodeManager){
			nodeManager.setActiveState(areaInfo.isActive());
			return nodeManager;
		}
		if(areaInfo.isActive()){
			nodeManager = new ChannelNodeManager(senderGroup, areaInfo.getIp(), areaInfo.getPort());
			mgrMap.put(areaInfo.getId(), nodeManager);
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
}
