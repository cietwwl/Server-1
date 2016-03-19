package com.bm.rank.arena;

import com.bm.rank.RankingJacksonExtension;
import com.rw.fsutil.ranking.RankingEntry;

public class ArenaSettleExtension extends
		RankingJacksonExtension<ArenaSettleComparable, ArenaSettlement> {

	public ArenaSettleExtension() {
		super(ArenaSettleComparable.class, ArenaSettlement.class);
	}

	@Override
	public void notifyEntryEvicted(
			RankingEntry<ArenaSettleComparable, ArenaSettlement> entry) {
	}

	@Override
	public <P> ArenaSettlement newEntryExtension(String key, P customParam) {
		Integer career = (Integer) customParam;
		ArenaSettlement settlement = new ArenaSettlement();
		settlement.setCareer(career);
		return settlement;
	}

}
