package com.bm.rank.arena;

public class ArenaSettleComparable implements Comparable<ArenaSettleComparable>{
	
	private int ranking;// 排名

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	@Override
	public int compareTo(ArenaSettleComparable o) {
		if(ranking > o.ranking){
			return -1;
		}else if(ranking < o.ranking){
			return 1;
		}
		return 0;
	}

}
