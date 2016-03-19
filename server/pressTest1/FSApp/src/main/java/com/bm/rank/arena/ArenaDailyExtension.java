package com.bm.rank.arena;

import com.bm.rank.RankingJacksonExtension;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

/**
 * 每日排行榜扩展
 * @author Jamaz
 *
 */
public class ArenaDailyExtension extends RankingJacksonExtension<ArenaRankingComparable, RankingLevelData>{

	public ArenaDailyExtension(){
		super(ArenaRankingComparable.class, RankingLevelData.class);
	}
	
	@Override
	public void notifyEntryEvicted(RankingEntry<ArenaRankingComparable, RankingLevelData> entry) {
		
	}

	@Override
	public <P> RankingLevelData newEntryExtension(String key, P customParam) {
		return (RankingLevelData)customParam;
	}

}
