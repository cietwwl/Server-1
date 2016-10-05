package com.bm.rank.groupCompetition.groupRank;


public class GCompFightingComparable implements Comparable<GCompFightingComparable> {
	private long groupFight; 	//帮派战力
	private int groupLevel;	//帮派等级
	private int lastRank; //帮派上次排名
	
	public GCompFightingComparable(){
		
	}
	
	public GCompFightingComparable(long groupFight, int groupLevel, int lastRank){
		this.groupFight = groupFight;
		this.groupLevel = groupLevel;
		this.lastRank = lastRank;
	}

	public long getGroupFight(){
		return this.groupFight;
	}
	
	public int getLastRank(){
		return lastRank;
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
