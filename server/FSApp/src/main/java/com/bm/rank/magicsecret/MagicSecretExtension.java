package com.bm.rank.magicsecret;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

public class MagicSecretExtension extends RankingJacksonExtension<MagicSecretComparable, RankingLevelData>{

	public MagicSecretExtension() {
		super(MagicSecretComparable.class, RankingLevelData.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<MagicSecretComparable, RankingLevelData> entry) {
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
		toData.setModelId(player.getModelId());
		toData.setJob(player.getCareer());
		toData.setSex(player.getSex());
		toData.setCareerLevel(player.getStarLevel());
		return toData;
	}

}
