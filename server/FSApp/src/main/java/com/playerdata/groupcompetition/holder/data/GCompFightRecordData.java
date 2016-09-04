package com.playerdata.groupcompetition.holder.data;

import java.util.LinkedList;

public class GCompFightRecordData {

	private int matchId;
	
	private LinkedList<GCompFightingRecord> record;

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public LinkedList<GCompFightingRecord> getRecord() {
		return record;
	}

	public void setRecord(LinkedList<GCompFightingRecord> record) {
		this.record = record;
	}
}
