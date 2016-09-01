package com.playerdata.groupcompetition.holder.data;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.data.IGCUnit;

/**
 * 
 * 帮派战的队伍数据
 * 
 * @author CHEN.P
 *
 */
@SynClass
public class GCompTeam implements IGCUnit {

	private String teamId; // 队伍的id
	private List<GCompTeamMember> members; // 队伍的成员
	
	/**
	 * 
	 * 设置队伍的id
	 * 
	 * @param pTeamId
	 */
	public void setId(String pTeamId) {
		this.teamId = pTeamId;
	}
	
	public List<GCompTeamMember> getMembers() {
		return members;
	}

	@Override
	public long getLastMatchTime() {
		return 0;
	}

	@Override
	public void setLastMatchTime(long time) {
		
	}

	@Override
	public int getTotalMatchTimes() {
		return 0;
	}

	@Override
	public void setTotalMatchTimes(int pTimes) {
		
	}

	@Override
	public String getId() {
		return teamId;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public int getTotalWinTimes() {
		return 0;
	}

	@Override
	public int getHighestContinuousWinTimes() {
		return 0;
	}

	@Override
	public int getCurrentContinousWinTimes() {
		return 0;
	}

	@Override
	public int getCurrentScore() {
		return 0;
	}

	@Override
	public int getCurrentScoreForGroup() {
		return 0;
	}
	
	@Override
	public String getIdOfLastCompetitor() {
		return null;
	}
	
	@Override
	public void setIdOfLastCompetitor(String competitorId) {
		
	}
}
