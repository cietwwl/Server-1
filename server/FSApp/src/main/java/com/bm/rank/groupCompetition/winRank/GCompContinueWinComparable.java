package com.bm.rank.groupCompetition.winRank;


public class GCompContinueWinComparable implements Comparable<GCompContinueWinComparable> {
	private int continueWin; 	//连胜场次
	private long lastRecordTime;	//上次创造纪录的时间
	
	public GCompContinueWinComparable(){
		
	}
	
	public GCompContinueWinComparable(int continueWin, long lastRecordTime){
		this.continueWin = continueWin;
		this.lastRecordTime = lastRecordTime;
	}

	public int getContinueWin(){
		return this.continueWin;
	}
	
	public long getLastRecordTime() {
		return lastRecordTime;
	}

	public void setLastRecordTime(long lastRecordTime) {
		this.lastRecordTime = lastRecordTime;
	}

	public void setContinueWin(int continueWin) {
		this.continueWin = continueWin;
	}

	@Override
	public int compareTo(GCompContinueWinComparable o) {
		if(continueWin > o.continueWin){
			return 1;
		}
		if(continueWin < o.continueWin){
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
