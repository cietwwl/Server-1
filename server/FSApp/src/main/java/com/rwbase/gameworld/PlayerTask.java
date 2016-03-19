package com.rwbase.gameworld;

import com.playerdata.Player;

/**
 * <pre>
 * 角色任务
 * </pre>
 * @author Jamaz
 *
 */
public interface PlayerTask {

	/**
	 * 运行角色任务的逻辑
	 * @param player
	 */
	public void run(Player player);
}

