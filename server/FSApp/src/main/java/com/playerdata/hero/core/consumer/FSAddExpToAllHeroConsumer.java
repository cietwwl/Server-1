package com.playerdata.hero.core.consumer;

import com.playerdata.Hero;
import com.playerdata.hero.IHeroConsumer;

public class FSAddExpToAllHeroConsumer implements IHeroConsumer {

	private long _exp;
	
	public FSAddExpToAllHeroConsumer(long exp) {
		this._exp = exp;
	}
	
	@Override
	public void apply(Hero hero) {
		hero.addHeroExp(_exp);
	}

}
