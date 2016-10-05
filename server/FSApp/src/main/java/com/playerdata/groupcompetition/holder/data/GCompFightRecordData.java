package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.fsutil.dao.annotation.CombineSave;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gc_fight_record")
public class GCompFightRecordData {

	@Id
	private int matchId;
	
	@CombineSave
	private ArrayList<GCompFightingRecord> record;

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public ArrayList<GCompFightingRecord> getRecord() {
		return record;
	}

	public void setRecord(ArrayList<GCompFightingRecord> record) {
		this.record = record;
	}
}
