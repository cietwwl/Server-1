package com.bm.rank.populatity;

import com.bm.rank.RankingJacksonExtension;
import com.rw.fsutil.ranking.RankingEntry;

/**
 * @Author HC
 * @date 2016年10月13日 下午4:50:43
 * @desc
 **/

public class PopularityRankExtension extends RankingJacksonExtension<PopularityRankComparable, PopularityData> {

	public PopularityRankExtension() {
		super(PopularityRankComparable.class, PopularityData.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<PopularityRankComparable, PopularityData> entry) {
	}

	@Override
	public <P> PopularityData newEntryExtension(String key, P customParam) {
		return (PopularityData) customParam;
	}
}