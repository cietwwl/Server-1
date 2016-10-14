package com.rw.handler.groupCompetition.data.events;

import java.util.ArrayList;
import java.util.List;

import com.rw.dataSyn.SynItem;
import com.rw.handler.groupCompetition.stageimpl.GCompAgainst;
import com.rw.handler.groupCompetition.util.GCEventsType;

public class GCompEventsData implements SynItem {

	private List<GCompAgainst> matches = new ArrayList<GCompAgainst>();
	private GCEventsType matchNumType; // 开始的比赛类型
	private int matchId; // 具体玩家的matchId
	private long startTime;
	private long endTime;
	private int session;
	
	@Override
	public String getId() {
		return null;
	}

	public List<GCompAgainst> getMatches() {
		return matches;
	}

	public GCEventsType getMatchNumType() {
		return matchNumType;
	}

	public int getMatchId() {
		return matchId;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getSession() {
		return session;
	}

}
