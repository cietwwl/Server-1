package com.bm.rank.fightingAll;

public class FightingComparable implements Comparable<FightingComparable> {

	private int fighting;
	@Override
	public int compareTo(FightingComparable o) {
		if (fighting < o.fighting) {
			return -1;
		}
		if (fighting > o.fighting) {
			return 1;
		}
		return 0;
	}

	public int getFighting() {
		return fighting;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

}
