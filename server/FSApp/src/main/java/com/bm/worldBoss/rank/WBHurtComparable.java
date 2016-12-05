package com.bm.worldBoss.rank;


public class WBHurtComparable implements Comparable<WBHurtComparable> {

	private long totalHurt; 	//总伤害值
	private long lastHurtTime;	//上次造成伤害的时间
	
	public WBHurtComparable(){
	}
	
	public WBHurtComparable(long totalHurt, long lastHurtTime){
		this.totalHurt = totalHurt;
		this.lastHurtTime = lastHurtTime;
	}

	
	public long getTotalHurt(){
		return this.totalHurt;
	}

	public long getLastHurtTime() {
		return lastHurtTime;
	}

	public void setLastHurtTime(long lastHurtTime) {
		this.lastHurtTime = lastHurtTime;
	}

	public void setTotalHurt(long totalHurt) {
		this.totalHurt = totalHurt;
	}

	@Override
	public int compareTo(WBHurtComparable o) {
		
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
