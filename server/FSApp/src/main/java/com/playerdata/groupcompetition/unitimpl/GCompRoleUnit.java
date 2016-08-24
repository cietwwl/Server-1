package com.playerdata.groupcompetition.unitimpl;

import com.playerdata.groupcompetition.data.IGCUnit;

/**
 * 
 * 帮派争霸的角色单位
 * 
 * @author CHEN.P
 *
 */
public class GCompRoleUnit implements IGCUnit {

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
		return null;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public String getIdOfLastCompetitor() {
		return null;
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
	public void setIdOfLastCompetitor(String competitorId) {
		// TODO Auto-generated method stub
		
	}

}
