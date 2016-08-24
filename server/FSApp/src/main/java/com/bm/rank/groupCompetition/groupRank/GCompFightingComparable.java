package com.bm.rank.groupCompetition.groupRank;


public class GCompFightingComparable implements Comparable<GCompFightingComparable> {
	private long groupFight; 	//帮派战力
	private int groupLevel;	//帮派等级
	
	public GCompFightingComparable(){
		
	}
	
	public GCompFightingComparable(long groupFight, int groupLevel){
		this.groupFight = groupFight;
		this.groupLevel = groupLevel;
	}

	public long getGroupFight(){
		return this.groupFight;
	}

	@Override
	public int compareTo(GCompFightingComparable o) {
		if(groupFight > o.groupFight){
			return 1;
		}
		if(groupFight < o.groupFight){
			return -1;
		}
		if(groupLevel > o.groupLevel){
			return 1;
		}
		if(groupLevel < o.groupLevel){
			return -1;
		}
		return 0;
	}
}
