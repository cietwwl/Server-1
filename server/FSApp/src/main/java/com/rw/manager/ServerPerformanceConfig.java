package com.rw.manager;

/**
 * 服务器性能配置
 * 
 * @author Jamaz
 *
 */
public class ServerPerformanceConfig {

	private final int playerCapacity; // 与玩家数量一一对应的容量
	private final int heroCapacity; // 与英雄数量一一对应的容量
	private final int itemCapacity; // 与道具数量一一对应的容量

	public ServerPerformanceConfig(int playerCapacity, int heroCapacity, int itemCapacity) {
		super();
		this.playerCapacity = playerCapacity;
		this.heroCapacity = heroCapacity;
		this.itemCapacity = itemCapacity;
	}

	public int getPlayerCapacity() {
		return playerCapacity;
	}

	public int getHeroCapacity() {
		return heroCapacity;
	}

	public int getItemCapacity() {
		return itemCapacity;
	}

}
