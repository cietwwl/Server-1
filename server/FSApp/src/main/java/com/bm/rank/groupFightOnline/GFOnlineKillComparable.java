package com.bm.rank.groupFightOnline;

public class GFOnlineKillComparable implements Comparable<GFOnlineKillComparable> {
	private int resourceID; 	//资源点
	private int totalKill; 	//杀敌数
	private long lastKillTime;	//上次杀敌时间
	
	public GFOnlineKillComparable(){
		
	}
	
	public GFOnlineKillComparable(int resourceID, int totalKill, long lastKillTime){
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
	public int compareTo(GFOnlineKillComparable o) {
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
