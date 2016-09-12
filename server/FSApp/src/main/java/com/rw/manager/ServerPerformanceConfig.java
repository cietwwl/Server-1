package com.rw.manager;

import java.util.Properties;

import com.common.HPCUtil;

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
	private final int latestLoginCount;
	private final int highLevelCount;

	public ServerPerformanceConfig(Properties props) {
		this.playerCapacity = Integer.parseInt(props.getProperty("playerCapacity"));
		this.heroCapacity = Integer.parseInt(props.getProperty("heroCapacity"));
		this.itemCapacity = Integer.parseInt(props.getProperty("itemCapacity"));
		this.latestLoginCount = HPCUtil.optionalParse(props.getProperty("latestLoginCount"), 200);
		this.highLevelCount = HPCUtil.optionalParse(props.getProperty("highLevelCount"), 100);
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

	public int getLatestLoginCount() {
		return latestLoginCount;
	}

	public int getHighLevelCount() {
		return highLevelCount;
	}

}
