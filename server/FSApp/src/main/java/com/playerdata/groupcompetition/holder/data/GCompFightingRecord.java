package com.playerdata.groupcompetition.holder.data;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompFightingRecord {
	private String id;
	private long time;
	private int matchId;
	private List<GCompPersonFightingRecord> personalFightingRecords;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public List<GCompPersonFightingRecord> getPersonalFightingRecords() {
		return personalFightingRecords;
	}
	
	public void setPersonalFightingRecords(List<GCompPersonFightingRecord> personalFightingRecords) {
		this.personalFightingRecords = personalFightingRecords;
	}
}
