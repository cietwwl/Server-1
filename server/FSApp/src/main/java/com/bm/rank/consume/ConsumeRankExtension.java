package com.bm.rank.consume;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;

public class ConsumeRankExtension extends RankingJacksonExtension<ConsumeComparable, RankingConsumeData>{

	public ConsumeRankExtension() {
		super(ConsumeComparable.class, RankingConsumeData.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<ConsumeComparable, RankingConsumeData> entry) {
	}

	@Override
	public <P> RankingConsumeData newEntryExtension(String key, P param) {
		if(param instanceof RankingConsumeData){
			return (RankingConsumeData)param;
		}
		Player player = (Player)param;
		RankingConsumeData toData = new RankingConsumeData();
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		return toData;
	}
}
