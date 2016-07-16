package com.playerdata.hero.core.consumer;

import com.playerdata.hero.IHero;
import com.playerdata.hero.IHeroConsumer;

public class FSCalculateAllFightingConsumer implements IHeroConsumer {

	private int _totalFighting;
	
	public int getTotalFighting() {
		return _totalFighting;
	}
	
	@Override
	public void apply(IHero hero) {
		_totalFighting += hero.getFighting();
	}

}
