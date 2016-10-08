package com.playerdata.hero.core.consumer;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.hero.IHeroConsumer;

public class FSGetMultipleHerosConsumer implements IHeroConsumer {

	private List<Hero> resultHeros;
	private List<String> targetHeroIds;
	
	public FSGetMultipleHerosConsumer(List<String> pTargetHeroIds) {
		this.targetHeroIds = new ArrayList<String>(pTargetHeroIds);
		this.resultHeros = new ArrayList<Hero>();
	}
	
	@Override
	public void apply(Hero hero) {
		if(targetHeroIds.contains(hero.getId())) {
			resultHeros.add(hero);
		}
	}
	
	public List<Hero> getResultHeros() {
		return resultHeros;
	}

}
