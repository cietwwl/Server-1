package com.playerdata.groupcompetition.holder.data;

public class GCompGroupTotalScoreRecord implements Comparable<GCompGroupTotalScoreRecord> {

	private GCompGroupScoreRecord _currentRecord; // 当前
	private int _totalScore; // 总积分
	private long _fighting; // 战斗力

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
