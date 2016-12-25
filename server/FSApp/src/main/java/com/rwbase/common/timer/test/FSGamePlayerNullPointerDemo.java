package com.rwbase.common.timer.test;

import com.playerdata.Player;
import com.rwbase.common.timer.IPlayerOperable;

public class FSGamePlayerNullPointerDemo implements IPlayerOperable {

	@Override
	public boolean isInterestingOn(Player player) {
		return true;
	}

	@Override
	public void operate(Player player) {
		System.out.println("null pointer : " + player.getUserId());
		throw new NullPointerException("player.getUserId()=" + player.getUserId());
	}

}
