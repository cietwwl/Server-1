package com.playerdata.hero.core.consumer;

import com.playerdata.Hero;
import com.playerdata.hero.IHeroConsumer;

public class FSCalculateAllFightingConsumer implements IHeroConsumer {

	private int _totalFighting;
	
	public int getTotalFighting() {
		return _totalFighting;
	}
	
	@Override
	public void apply(Hero hero) {
		_totalFighting += hero.getFighting();
	}

}
