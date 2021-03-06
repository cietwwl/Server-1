package com.bm.rank.populatity;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

/**
 * @Author HC
 * @date 2016年10月13日 下午4:50:43
 * @desc
 **/

public class PopularityRankExtension extends RankingJacksonExtension<PopularityRankComparable, RankingLevelData> {

	public PopularityRankExtension() {
		super(PopularityRankComparable.class, RankingLevelData.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<PopularityRankComparable, RankingLevelData> entry) {
	}

	@Override
	public <P> RankingLevelData newEntryExtension(String key, P param) {
		if (param instanceof RankingLevelData) {
			return (RankingLevelData) param;
		}
		Player player = (Player) param;
		RankingLevelData toData = new RankingLevelData();
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		toData.setLevel(player.getLevel());
		toData.setExp(player.getExp());
		toData.setFightingAll(player.getHeroMgr().getFightingAll(player));
		toData.setFightingTeam(player.getHeroMgr().getFightingTeam(player));
		toData.setUserHead(player.getHeadImage());
		toData.setHeadbox(player.getHeadFrame());
		toData.setModelId(player.getModelId());
		toData.setJob(player.getCareer());
		toData.setSex(player.getSex());
		toData.setCareerLevel(player.getStarLevel());
		toData.setVip(player.getVip());
		toData.setMagicCfgId(player.getMagic().getModelId());
		return toData;
	}
}