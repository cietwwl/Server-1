package com.bm.rank.groupCompetition;

public class GCompKillComparable implements Comparable<GCompKillComparable> {
	private int resourceID; 	//资源点
	private int totalKill; 	//杀敌数
	private long lastKillTime;	//上次杀敌时间
	
	public GCompKillComparable(){
		
	}
	
	public GCompKillComparable(int resourceID, int totalKill, long lastKillTime){
		this.resourceID = resourceID;
		this.totalKill = totalKill;
		this.lastKillTime = lastKillTime;
	}

	public int getResourceID(){
		return this.resourceID;
	}
	
	public int getTotalKill(){
		return this.totalKill;
	}

	@Override
	public int compareTo(GCompKillComparable o) {
		if(resourceID < o.resourceID){
			return 1;
		}
		if(resourceID > o.resourceID){
			return -1;
		}
		if(totalKill > o.totalKill){
			return 1;
		}
		if(totalKill < o.totalKill){
			return -1;
		}
		if(lastKillTime > o.lastKillTime){
			return 1;
		}
		if(lastKillTime < o.lastKillTime){
			return -1;
		}
		return 0;
	}
}
