package com.playerdata.groupcompetition.matching;

import com.playerdata.groupcompetition.holder.data.GCompTeam;

/**
 * 
 * 帮派争霸的匹配数据
 * 
 * @author CHEN.P
 *
 */
class MatchingData {
	
	public MatchingData(GCompTeam team, String groupId, int matchId) {
		this.teamId = team.getTeamId();
		this.groupId = groupId;
		this.lv = team.getLv();
		this.matchId = matchId;
		this.battleTimes = team.getBattleTimes();
		this.submitTime = System.currentTimeMillis();
	}
	
	private String teamId; // 队伍id
	private String groupId; // 帮派id
	private int lv; // 等级
	private int matchId;
	private int battleTimes; // 参与战斗的次数
	private long submitTime; // 提交的时间
	private boolean cancel;

	public String getTeamId() {
		return teamId;
	}

	public String getGroupId() {
		return groupId;
	}

	public int getLv() {
		return lv;
	}

	public int getMatchId() {
		return matchId;
	}

	public int getBattleTimes() {
		return battleTimes;
	}

	public long getSubmitTime() {
		return submitTime;
	}
	
	public void setCancel(boolean value) {
		this.cancel = value;
	}

	public boolean isCancel() {
		return cancel;
	}
	
	@Override
	public String toString() {
		return "MatchingData [teamId=" + teamId + ", groupId=" + groupId + ", lv=" + lv + "]";
	}
}
