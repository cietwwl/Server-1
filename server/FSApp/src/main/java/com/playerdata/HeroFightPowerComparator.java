package com.playerdata;

import java.util.Comparator;

public class HeroFightPowerComparator {
	private static Comparator<Hero> comparator = new Comparator<Hero>() {

		public int compare(Hero o1, Hero o2) {
			if (o1.getFighting() < o2.getFighting())
				return 1;
			if (o1.getFighting() > o2.getFighting())
				return -1;
			return 0;
		}
	};
	public static Comparator<Hero> getInstance(){
		return comparator;
	}
}
