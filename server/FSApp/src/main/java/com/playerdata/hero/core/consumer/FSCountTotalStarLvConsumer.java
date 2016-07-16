package com.playerdata.hero.core.consumer;

import com.playerdata.hero.IHero;
import com.playerdata.hero.IHeroConsumer;

public class FSCountTotalStarLvConsumer implements IHeroConsumer {

	private int _totalStarLv;
	
	public int getTotalStarLv() {
		return _totalStarLv;
	}
	
	@Override
	public void apply(IHero hero) {
		this._totalStarLv += hero.getStarLevel();
	}

}
