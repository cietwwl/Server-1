package com.playerdata.groupcompetition.holder.data;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompFightingRecord {

	private long time;
	private List<GCompPersonFightingRecord> personalFightingRecords;
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public List<GCompPersonFightingRecord> getPersonalFightingRecords() {
		return personalFightingRecords;
	}
	
	public void setPersonalFightingRecords(List<GCompPersonFightingRecord> personalFightingRecords) {
		this.personalFightingRecords = personalFightingRecords;
	}

}
