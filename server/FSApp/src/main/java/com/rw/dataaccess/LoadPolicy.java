package com.rw.dataaccess;

/**
 * <pre>
 * 缓存加载策略
 * </pre>
 * @author Jamz
 *
 */
public enum LoadPolicy {
	
	/**
	 * 玩家被查询时加载
	 */
	PLAYER_QUERY,
	/**
	 * 玩家主动登录时加载
	 */
	PLAYER_LOGIN,
	/**
	 * 单独被查询时加载
	 */
	QUERY
}
