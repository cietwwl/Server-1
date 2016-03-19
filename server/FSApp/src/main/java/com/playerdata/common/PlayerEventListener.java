package com.playerdata.common;

import com.playerdata.Player;

public interface PlayerEventListener {

	/**
	 * 玩家创建的通知
	 * @param player
	 */
	public void notifyPlayerCreated(Player player);

	/**
	 * 玩家登陆通知
	 * @param player
	 */
	public void notifyPlayerLogin(Player player);
	
	/**
	 * 从数据加载到内存的初始化工作
	 * @param player
	 */
	public void init(Player player);
}
