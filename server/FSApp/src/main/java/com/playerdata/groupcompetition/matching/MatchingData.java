package com.playerdata.groupcompetition.matching;

import com.playerdata.groupcompetition.holder.data.GCompTeam;
import com.playerdata.groupcompetition.util.GCompUtil;

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
		this.deadline = System.currentTimeMillis() + GCompUtil.getMatchingTimeoutMillis();
	}
	
	private String teamId; // 队伍id
	private String groupId; // 帮派id
	private int lv; // 等级
	private int matchId;
	private int battleTimes; // 参与战斗的次数
//	private long submitTime; // 提交的时间
	private long deadline; // 最后时间
	private volatile boolean cancel;
	private volatile boolean matched;

	public String getTeamId() {
		return teamId;
	}

	public String getGroupId() {
		return groupId;
	}
	
	public void setLv(int pLv) {
		this.lv = pLv;
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

	public long getDeadline() {
		return deadline;
	}
	
	public void setCancel(boolean value) {
		this.cancel = value;
	}
	
	public void setDeadline(long pDeadline) {
		this.deadline = pDeadline;
	}

	public boolean isCancel() {
		return cancel;
	}
	
	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}
	
	@Override
	public String toString() {
		return "MatchingData [teamId=" + teamId + ", groupId=" + groupId + ", lv=" + lv + "]";
	}
}
