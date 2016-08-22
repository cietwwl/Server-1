package com.bm.rank.groupCompetition;


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

	@Override
	public int compareTo(GCompContinueWinComparable o) {
		if(continueWin > o.continueWin){
			return 1;
		}
		if(continueWin < o.continueWin){
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
