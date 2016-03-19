package com.rw.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * 服务器列表信息
 * @author HC
 * @date 2015年12月15日 下午5:26:56
 * @Description 
 */
public class ServerListFactory {
	/** 服务器的列表信息 */
	private static ConcurrentHashMap<Integer, ServerInfo> serverMap = new ConcurrentHashMap<Integer, ServerInfo>();
	/** 用户在那些服务器有账户 */
	private static ConcurrentHashMap<String, List<Integer>> roleInServer = new ConcurrentHashMap<String, List<Integer>>();

	/**
	 * 初始化服务器列表
	 * 
	 * @param serverMap
	 */
	public static synchronized void initServerList(Map<Integer, ServerInfo> serverMap) {
		if (serverMap.isEmpty()) {
			return;
		}

		serverMap.putAll(serverMap);
	}

	/**
	 * 获取所有的服务器列表
	 * 
	 * @return
	 */
	public static List<ServerInfo> getServerList() {
		return new ArrayList<ServerInfo>(serverMap.values());
	}

	/**
	 * 获取服务器信息
	 * 
	 * @param serverId
	 * @return
	 */
	public static ServerInfo getServerInfo(int serverId) {
		return serverMap.get(serverId);
	}

	/**
	 * 是否已经有初始化了
	 * 
	 * @return
	 */
	public static synchronized boolean isInit() {
		return !serverMap.isEmpty();
	}
}