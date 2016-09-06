package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;

@SynClass
public class GCompEventsSynData {

	@SuppressWarnings("unused")
	private List<GCompAgainst> matches = new ArrayList<GCompAgainst>();
	@SuppressWarnings("unused")
	private GCEventsType matchNumType; // 开始的比赛类型
	@SuppressWarnings("unused")
	private int matchId; // 具体玩家的matchId
	
	public void setMatches(List<GCompAgainst> matches) {
		this.matches = matches;
	}
	
	public void setMatchNumType(GCEventsType matchNumType) {
		this.matchNumType = matchNumType;
	}
	
	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}
}
