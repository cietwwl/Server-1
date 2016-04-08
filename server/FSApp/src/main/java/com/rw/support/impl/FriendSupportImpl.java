package com.rw.support.impl;

import com.playerdata.Player;
import com.rw.support.FriendSupport;

public class FriendSupportImpl implements FriendSupport{

	@Override
	public void notifyFriendInfoChanged(Player player) {
		player.getFriendMgr().onPlayerChange(player);
	}

}
