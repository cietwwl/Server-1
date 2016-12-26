package com.playerdata.randomname;

import com.bm.player.PlayerChangePopertyObserver;
import com.bm.player.PlayerChangePopertySubscribe;
import com.playerdata.Player;

public class RandomNamePlayerChange extends PlayerChangePopertySubscribe {

	public RandomNamePlayerChange(PlayerChangePopertyObserver observer) {
		super(observer);
	}

	@Override
	public void playerChangeName(Player p) {
		RandomNameMgr.getInstance().destroyName(p.getUserName());
	}

	@Override
	public void playerChangeLevel(Player p) {
		
	}

	@Override
	public void playerChangeVipLevel(Player p) {
		
	}

	@Override
	public void playerChangeTemplateId(Player p) {
		
	}

	@Override
	public void playerChangeHeadIcon(Player p) {
		
	}

	@Override
	public void playerChangeHeadBox(Player p) {
		
	}

}
