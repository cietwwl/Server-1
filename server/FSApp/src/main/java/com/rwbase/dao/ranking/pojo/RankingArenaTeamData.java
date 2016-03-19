package com.rwbase.dao.ranking.pojo;

public class RankingArenaTeamData{
	private int winCount;
	private RankingTeamData teamData;

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public RankingTeamData getTeamData() {
		return teamData;
	}

	public void setTeamData(RankingTeamData teamData) {
		this.teamData = teamData;
	}
}
