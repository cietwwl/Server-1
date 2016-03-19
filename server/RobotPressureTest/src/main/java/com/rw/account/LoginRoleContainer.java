package com.rw.account;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * 所有登录缓存角色的集合
 * @author HC
 * @date 2015年12月14日 下午3:27:10
 * @Description 
 */
public class LoginRoleContainer {
	/** 所有登录的角色 */
	private static ConcurrentHashMap<Integer, Map<String, Role>> loginGameRole = new ConcurrentHashMap<Integer, Map<String, Role>>();
	/** 所有登录的角色 */
	private static ConcurrentHashMap<String, Role> loginPlatformRole = new ConcurrentHashMap<String, Role>();

	/**
	 * 加入注册的新角色
	 * 
	 * @param role
	 */
	public static void putLoginRole(Role role) {
		if (role == null) {
			return;
		}

		loginPlatformRole.put(role.getAccountId(), role);
	}

	/**
	 * 获取角色
	 * 
	 * @param key
	 */
	public static Role getLoginPlatformRole(String key) {
		return loginPlatformRole.get(key);
	}

	/**
	 * 获取所有已经注册的角色信息
	 * 
	 * @return
	 */
	public static Enumeration<Role> getAllCreateRole() {
		return loginPlatformRole.elements();
	}

	/**
	 * 连接到真正的登录服务器
	 * 
	 * @param serverId
	 * @param role
	 */
	public static void serverLoginRole(int serverId, Role role) {
		if (role == null) {
			return;
		}

		Map<String, Role> map = loginGameRole.get(serverId);
		if (map == null) {
			map = new HashMap<String, Role>();
			loginGameRole.put(serverId, map);
		}

		String accountId = role.getAccountId();
		map.put(accountId, role);

		// 从平台连接中移除
		if (loginPlatformRole.containsKey(accountId)) {
			loginPlatformRole.remove(accountId);
		}
	}

	/**
	 * 获取服务器登录的角色信息
	 * 
	 * @param serverId
	 * @param accountId
	 * @return
	 */
	public static Role getLoginServerRole(int serverId, String accountId) {
		Map<String, Role> map = loginGameRole.get(serverId);
		if (map == null) {
			return null;
		}

		return map.get(accountId);
	}
}