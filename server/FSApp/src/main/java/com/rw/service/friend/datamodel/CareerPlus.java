package com.rw.service.friend.datamodel;

import com.playerdata.Player;

public class CareerPlus implements FactorExtractor{

	@Override
	public Integer extract(Player player) {
		return player.getCareer();
	}

}
