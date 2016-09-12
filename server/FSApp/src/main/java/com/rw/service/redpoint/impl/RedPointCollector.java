package com.rw.service.redpoint.impl;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

/**
 * 检查这个模块是否有红点
 * 
 * @author Jamaz
 *
 */
public interface RedPointCollector {

	/**
	 * 填充在前端展示的红点
	 * 基于Player实现，传入level和userId
	 * @param player
	 * @return
	 */
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level);

	/**
	 * 获取开放类型,未开放不会进行检查
	 * 
	 * @return
	 */
	public eOpenLevelType getOpenType();
}
