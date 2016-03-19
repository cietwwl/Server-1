package com.bm.rank;

import com.bm.rank.arena.ArenaRankingComparable;
import com.bm.rank.fightingAll.FightingComparable;
import com.bm.rank.level.LevelComparable;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

public class RankingCopyerFactory {

	static RankingLevelData copyLevelData(RankingLevelData data) {
		RankingLevelData toData = new RankingLevelData();
		toData.setUserId(data.getUserId());
		toData.setLevel(data.getLevel());
		toData.setExp(data.getExp());
		toData.setFightingAll(data.getFightingAll());
		toData.setFightingTeam(data.getFightingTeam());
		toData.setArenaPlace(data.getArenaPlace());
		toData.setUserName(data.getUserName());
		toData.setUserHead(data.getUserHead());
		toData.setJob(data.getJob());
		toData.setSex(data.getSex());
		toData.setCareerLevel(data.getCareerLevel());
		toData.setModelId(data.getModelId());
		toData.setRankLevel(data.getRankLevel());
		toData.setRankCount(data.getRankCount());
		return toData;
	}

	public static class LevelExtensionCopyer implements RankingEntityCopyer<LevelComparable, RankingLevelData> {

		@Override
		public LevelComparable copyComparable(LevelComparable cmp) {
			LevelComparable lc = new LevelComparable();
			lc.setExp(cmp.getExp());
			lc.setLevel(cmp.getLevel());
			return lc;
		}

		@Override
		public RankingLevelData copyExtension(RankingLevelData ext) {
			return copyLevelData(ext);
		}

	}

	public static class FightingAllExtension implements RankingEntityCopyer<FightingComparable,RankingLevelData>{

		@Override
		public FightingComparable copyComparable(FightingComparable cmp) {
			FightingComparable fc = new FightingComparable();
			fc.setFighting(cmp.getFighting());
			return fc;
		}

		@Override
		public RankingLevelData copyExtension(RankingLevelData ext) {
			return copyLevelData(ext);
		}
		
	}
	
	public static class ArenaDailyCopyer implements RankingEntityCopyer<ArenaRankingComparable, RankingLevelData> {

		@Override
		public ArenaRankingComparable copyComparable(ArenaRankingComparable cmp) {
			ArenaRankingComparable c = new ArenaRankingComparable();
			c.setRanking(cmp.getRanking());
			return c;
		}

		@Override
		public RankingLevelData copyExtension(RankingLevelData ext) {
			return copyLevelData(ext);
		}

	}
	
	private static LevelExtensionCopyer levelExtCopyer = new LevelExtensionCopyer();
	private static FightingAllExtension fightingCopyer = new FightingAllExtension();
	private static ArenaDailyCopyer arenaCopyer = new ArenaDailyCopyer();

	public static LevelExtensionCopyer getLevelExtCopyer() {
		return levelExtCopyer;
	}
	
	public static FightingAllExtension getFightingCopyer() {
		return fightingCopyer;
	}
	
	public static ArenaDailyCopyer getArenaCopyer() {
		return arenaCopyer;
	}

}
