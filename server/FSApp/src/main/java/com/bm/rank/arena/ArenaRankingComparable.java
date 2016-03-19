package com.bm.rank.arena;

public class ArenaRankingComparable implements Comparable<ArenaRankingComparable> {

	private int ranking;

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	@Override
	public int compareTo(ArenaRankingComparable o) {
		if(ranking < o.ranking){
			return 1;
		}
		if(ranking > o.ranking){
			return -1;
		}
		return 0;
	}

}
