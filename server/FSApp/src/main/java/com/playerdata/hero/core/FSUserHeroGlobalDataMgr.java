package com.playerdata.hero.core;

import com.rwbase.dao.hero.FSUserHeroGlobalDataDAO;
import com.rwbase.dao.hero.pojo.FSUserHeroGlobalData;

public class FSUserHeroGlobalDataMgr {

	private static FSUserHeroGlobalDataMgr _INSTANCE = new FSUserHeroGlobalDataMgr();
	
	public static FSUserHeroGlobalDataMgr getInstance() {
		return _INSTANCE;
	}
	
	protected FSUserHeroGlobalDataMgr() {}
	
	public void notifySingleFightingChange(String userId, String heroId, int newSingleValue, int preSingleValue) {
		if (newSingleValue != preSingleValue) {
			FSUserHeroGlobalData globalData = FSUserHeroGlobalDataDAO.getInstance().get(userId);
			if (globalData.getFightingTeamHeroIdsRO().contains(heroId)) {
				globalData.setFightingTeam(globalData.getFightingTeam() + (newSingleValue - preSingleValue));
				FSUserHeroGlobalDataDAO.getInstance().update(globalData);
			}
		}
	}
}
