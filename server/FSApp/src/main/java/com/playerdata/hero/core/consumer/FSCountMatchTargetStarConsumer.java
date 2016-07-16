package com.playerdata.hero.core.consumer;

import com.playerdata.hero.IHero;
import com.playerdata.hero.IHeroConsumer;

public class FSCountMatchTargetStarConsumer implements IHeroConsumer {

	private int _targetStarLv;
	private int _countResult;

	public FSCountMatchTargetStarConsumer(int pTargetStarLv) {
		this._targetStarLv = pTargetStarLv;
	}
	
	public int getCountResult() {
		return _countResult;
	}

	@Override
	public void apply(IHero hero) {
		if (hero.getStarLevel() >= _targetStarLv) {
			_countResult++;
		}
	}
	
}
