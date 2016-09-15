package com.playerdata.hero.core.consumer;

import com.playerdata.Hero;
import com.playerdata.hero.IHeroConsumer;
import com.playerdata.hero.core.FSHeroMgr;

public class FSAddExpToAllHeroConsumer implements IHeroConsumer {

	private long _exp;
	
	public FSAddExpToAllHeroConsumer(long exp) {
		this._exp = exp;
	}
	
	@Override
	public void apply(Hero hero) {
		if (hero.isMainRole()) {
			FSHeroMgr.getInstance().getOwnerOfHero(hero).addUserExp(_exp);
		} else {
			FSHeroMgr.getInstance().addHeroExp(hero, _exp);
		}
	}

}
