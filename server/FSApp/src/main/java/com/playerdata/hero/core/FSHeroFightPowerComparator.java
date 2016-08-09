package com.playerdata.hero.core;

import java.util.Comparator;

import com.playerdata.Hero;

public class FSHeroFightPowerComparator implements Comparator<Hero>{

	public static final FSHeroFightPowerComparator INSTANCE = new FSHeroFightPowerComparator();
	
	protected FSHeroFightPowerComparator() {}
	
	public int compare(Hero o1, Hero o2) {
		if (o1.getFighting() < o2.getFighting())
			return 1;
		if (o1.getFighting() > o2.getFighting())
			return -1;
		return 0;
	}
}
