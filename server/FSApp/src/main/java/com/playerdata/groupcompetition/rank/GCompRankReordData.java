package com.playerdata.groupcompetition.rank;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.bm.rank.groupCompetition.killRank.GCompKillItem;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreItem;
import com.bm.rank.groupCompetition.winRank.GCompContinueWinItem;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gc_rank_data")
public class GCompRankReordData {
	
	@Id
	private String  rankID;
	
	@CombineSave
	private List<GCompKillItem> killRecord;
	
	@CombineSave
	private List<GCompContinueWinItem> winRecord;
	
	@CombineSave
	private List<GCompScoreItem> scoreRecord;

	public String getRankID() {
		return rankID;
	}

	public void setRankID(String rankID) {
		this.rankID = rankID;
	}

	public List<GCompKillItem> getKillRecord() {
		return killRecord;
	}

	public void setKillRecord(List<GCompKillItem> killRecord) {
		this.killRecord = killRecord;
	}

	public List<GCompContinueWinItem> getWinRecord() {
		return winRecord;
	}

	public void setWinRecord(List<GCompContinueWinItem> winRecord) {
		this.winRecord = winRecord;
	}

	public List<GCompScoreItem> getScoreRecord() {
		return scoreRecord;
	}

	public void setScoreRecord(List<GCompScoreItem> scoreRecord) {
		this.scoreRecord = scoreRecord;
	}
}
