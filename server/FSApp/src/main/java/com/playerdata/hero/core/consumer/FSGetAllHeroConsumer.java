package com.playerdata.hero.core.consumer;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.hero.IHero;
import com.playerdata.hero.IHeroConsumer;

public class FSGetAllHeroConsumer implements IHeroConsumer {

	private List<IHero> _targets = new ArrayList<IHero>();
	private IHero _main;
	private boolean _includeMain;
	
	public FSGetAllHeroConsumer(boolean includeMain) {
		this._includeMain = includeMain;
	}
	
	public List<IHero> getResultList() {
		return _targets;
	}
	
	public IHero getMainHero() {
		return _main;
	}
	
	@Override
	public void apply(IHero hero) {
		if(hero.isMainRole()) {
			_main = hero;
			if(!_includeMain) {
				return;
			}
		}
		_targets.add(hero);
	}

}
