package com.rw.service.redpoint.impl;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;

/**
 * 检查这个模块是否有红点
 * @author Jamaz
 *
 */
public interface RedPointCollector {

	/**
	 * 获取所有红点
	 * @param player
	 * @return
	 */
	public void fillRedPoints(Player player,Map<RedPointType, List<String>> map);
}
