package com.rw.service.friend;

import com.playerdata.Player;

public class TempFriendItem {

	private final Player player;
	private final int weight;

	public TempFriendItem(Player player, int weight) {
		super();
		this.player = player;
		this.weight = weight;
	}

	public Player getPlayer() {
		return player;
	}

	public int getWeight() {
		return weight;
	}
}
