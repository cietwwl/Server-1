package com.playerdata.hero.core.consumer;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.hero.IHeroConsumer;

public class FSGetAllHeroConsumer implements IHeroConsumer {

	private List<Hero> _targets = new ArrayList<Hero>();
	private Hero _main;
	private boolean _includeMain;
	
	public FSGetAllHeroConsumer(boolean includeMain) {
		this._includeMain = includeMain;
	}
	
	public List<Hero> getResultList() {
		return _targets;
	}
	
	public Hero getMainHero() {
		return _main;
	}
	
	@Override
	public void apply(Hero hero) {
		if(hero.isMainRole()) {
			_main = hero;
			if(!_includeMain) {
				return;
			}
		}
		_targets.add(hero);
	}

}
