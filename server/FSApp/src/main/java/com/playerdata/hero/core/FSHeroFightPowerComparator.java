package com.playerdata.hero.core;

import java.util.Comparator;

import com.playerdata.hero.IHero;

public class FSHeroFightPowerComparator implements Comparator<IHero>{

	public static final FSHeroFightPowerComparator INSTANCE = new FSHeroFightPowerComparator();
	
	protected FSHeroFightPowerComparator() {}
	
	public int compare(IHero o1, IHero o2) {
		if (o1.getFighting() < o2.getFighting())
			return 1;
		if (o1.getFighting() > o2.getFighting())
			return -1;
		return 0;
	}
}
