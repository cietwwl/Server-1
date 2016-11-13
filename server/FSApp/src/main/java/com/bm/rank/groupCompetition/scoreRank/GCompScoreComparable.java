package com.bm.rank.groupCompetition.scoreRank;


public class GCompScoreComparable implements Comparable<GCompScoreComparable> {
	private int totalScore; 	//总分数
	private long lastRecordTime;	//上次创造纪录的时间
	
	public GCompScoreComparable(){
		
	}
	
	public GCompScoreComparable(int totalScore, long lastRecordTime){
		this.totalScore = totalScore;
		this.lastRecordTime = lastRecordTime;
	}

	public int getTotalScore(){
		return this.totalScore;
	}
	
	public long getLastRecordTime() {
		return lastRecordTime;
	}

	public void setLastRecordTime(long lastRecordTime) {
		this.lastRecordTime = lastRecordTime;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	@Override
	public int compareTo(GCompScoreComparable o) {
		if(totalScore > o.totalScore){
			return 1;
		}
		if(totalScore < o.totalScore){
			return -1;
		}
		if(lastRecordTime > o.lastRecordTime){
			return -1;
		}
		if(lastRecordTime < o.lastRecordTime){
			return 1;
		}
		return 0;
	}
}
