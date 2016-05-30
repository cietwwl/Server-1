package com.bm.rank.fightingAll;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

public class FightingExtension extends RankingJacksonExtension<FightingComparable, RankingLevelData>{

	public FightingExtension(){
		super(FightingComparable.class, RankingLevelData.class);
	}
	
	@Override
	public void notifyEntryEvicted(RankingEntry<FightingComparable, RankingLevelData> entry) {
	}

	@Override
	public <P> RankingLevelData newEntryExtension(String key, P param) {
		if(param instanceof RankingLevelData){
			return (RankingLevelData)param;
		}
		
		Player player = (Player)param;
		RankingLevelData toData = new RankingLevelData();
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		toData.setLevel(player.getLevel());
		toData.setExp(player.getExp());
		toData.setFightingAll(player.getHeroMgr().getFightingAll());
		toData.setFightingTeam(player.getHeroMgr().getFightingTeam());
		toData.setUserHead(player.getHeadImage());
		toData.setHeadbox(player.getHeadFrame());
		toData.setModelId(player.getModelId());
		toData.setJob(player.getCareer());
		toData.setSex(player.getSex());
		toData.setCareerLevel(player.getStarLevel());
		return toData;
	}

}
