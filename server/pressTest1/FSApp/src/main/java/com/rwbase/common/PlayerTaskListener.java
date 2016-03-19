package com.rwbase.common;

import com.playerdata.Player;

/**
 * <pre>
 * 玩家任务监听器
 * </pre>
 * @author Jamaz
 *
 */
public interface PlayerTaskListener {

	/**
	 * 任务完成通知
	 * @param player
	 */
	public void notifyTaskCompleted(Player player);
}
