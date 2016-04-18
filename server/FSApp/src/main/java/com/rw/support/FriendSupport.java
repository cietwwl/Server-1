package com.rw.support;

import com.playerdata.Player;

public interface FriendSupport {

	/**
	 * 通知玩家好友数据数据改变
	 * @param player
	 */
	public void notifyFriendInfoChanged(Player player);
}
