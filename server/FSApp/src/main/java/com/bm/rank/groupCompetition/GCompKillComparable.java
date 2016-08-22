package com.bm.rank.groupCompetition;

public class GCompKillComparable implements Comparable<GCompKillComparable> {
	private int totalKill; 	//总的杀敌数
	private long lastRecordTime;	//上次创造纪录的时间
	
	public GCompKillComparable(){
		
	}
	
	public GCompKillComparable(int totalKill, long lastRecordTime){
		this.totalKill = totalKill;
		this.lastRecordTime = lastRecordTime;
	}

	public int getTotalKill(){
		return this.totalKill;
	}

	@Override
	public int compareTo(GCompKillComparable o) {
		if(totalKill > o.totalKill){
			return 1;
		}
		if(totalKill < o.totalKill){
			return -1;
		}
		if(lastRecordTime > o.lastRecordTime){
			return 1;
		}
		if(lastRecordTime < o.lastRecordTime){
			return -1;
		}
		return 0;
	}
}
