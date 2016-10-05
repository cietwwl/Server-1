package com.playerdata.groupcompetition.holder.data;

public class GCompGroupTotalScoreRecord implements Comparable<GCompGroupTotalScoreRecord> {

	private GCompGroupScoreRecord _currentRecord; // 当前
	private int _totalScore; // 总积分
	private long _fighting; // 战斗力
	private int _ranking; 
	
	public static GCompGroupTotalScoreRecord createEmpty() {
		GCompGroupTotalScoreRecord instance = new GCompGroupTotalScoreRecord();
		instance._currentRecord = GCompGroupScoreRecord.createNew("", "", "");
		instance._totalScore = 0;
		instance._fighting = 0l;
		return instance;
	}

	public GCompGroupScoreRecord getCurrentRecord() {
		return _currentRecord;
	}

	public void setCurrentRecord(GCompGroupScoreRecord pCurrentRecord) {
		this._currentRecord = pCurrentRecord;
	}

	public int getTotalScore() {
		return _totalScore;
	}

	public void setTotalScore(int pTotalScore) {
		this._totalScore = pTotalScore;
	}
	
	public long getFighting() {
		return _fighting;
	}
	
	public void setFighting(long pFighting) {
		this._fighting = pFighting;
	}

	public int getRanking() {
		return _ranking;
	}

	public void setRanking(int pRanking) {
		this._ranking = pRanking;
	}
	
	@Override
	public int compareTo(GCompGroupTotalScoreRecord o) {
		if (o == null) {
			return -1;
		}
		if (this._totalScore > o._totalScore) {
			return -1;
		} else {
			int myCurrentScore = _currentRecord.getScore();
			int otherCurrentScore = o._currentRecord.getScore();
			return myCurrentScore > otherCurrentScore ? -1 : myCurrentScore == otherCurrentScore ? 0 : 1;
		}
	}

}
