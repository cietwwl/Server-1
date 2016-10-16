package com.playerdata.hero.core.consumer;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.hero.IHeroConsumer;
import com.playerdata.hero.core.FSHeroMgr;

public class FSAddExpToAllHeroConsumer implements IHeroConsumer {

	private long _exp;
	private Player _player;
	
	public FSAddExpToAllHeroConsumer(Player player, long exp) {
		this._exp = exp;
		this._player = player;
	}
	
	@Override
	public void apply(Hero hero) {
		if (hero.isMainRole()) {
			_player.addUserExp(_exp);
		} else {
			FSHeroMgr.getInstance().addHeroExp(_player, hero, _exp);
		}
	}

}
