package com.playerdata.groupcompetition.holder.data;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.playerdata.groupcompetition.util.GCEventsType;

@SynClass
public class GCompEventsSynData {

//	@SuppressWarnings("unused")
	private List<GCompAgainst> matches = new ArrayList<GCompAgainst>();
//	@SuppressWarnings("unused")
	private GCEventsType matchNumType; // 开始的比赛类型
//	@SuppressWarnings("unused")
	private int matchId; // 具体玩家的matchId
	private long startTime;
	private long endTime;
	private int session;
	
	public void addMatches(List<GCompAgainst> matches) {
		this.matches.addAll(matches);
	}
	
	public void setMatchNumType(GCEventsType matchNumType) {
		this.matchNumType = matchNumType;
	}
	
	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getEndTime() {
		return endTime;
	}
	
	public int getSession() {
		return session;
	}

	public void setSession(int session) {
		this.session = session;
	}

	@Override
	public String toString() {
		return "GCompEventsSynData [matches=" + matches + ", matchNumType=" + matchNumType + ", matchId=" + matchId + ", startTime=" + startTime + ", endTime=" + endTime + ", session=" + session
				+ "]";
	}
	
}
