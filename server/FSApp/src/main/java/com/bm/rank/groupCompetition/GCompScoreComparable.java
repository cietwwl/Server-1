package com.bm.rank.groupCompetition;


public class GCompScoreComparable implements Comparable<GCompScoreComparable> {
	private int resourceID; 	//资源点
	private int totalHurt; 	//总伤害值
	private long lastHurtTime;	//上次造成伤害的时间
	
	public GCompScoreComparable(){
		
	}
	
	public GCompScoreComparable(int resourceID, int totalHurt, long lastHurtTime){
		this.resourceID = resourceID;
		this.totalHurt = totalHurt;
		this.lastHurtTime = lastHurtTime;
	}

	public int getResourceID(){
		return this.resourceID;
	}
	
	public int getTotalHurt(){
		return this.totalHurt;
	}

	@Override
	public int compareTo(GCompScoreComparable o) {
		if(resourceID < o.resourceID){
			return 1;
		}
		if(resourceID > o.resourceID){
			return -1;
		}
		if(totalHurt > o.totalHurt){
			return 1;
		}
		if(totalHurt < o.totalHurt){
			return -1;
		}
		if(lastHurtTime > o.lastHurtTime){
			return 1;
		}
		if(lastHurtTime < o.lastHurtTime){
			return -1;
		}
		return 0;
	}
}
