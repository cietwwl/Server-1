package com.bm.rank.charge;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.charge.dao.ChargeInfo;
import com.rw.fsutil.ranking.RankingEntry;

public class ChargeRankExtension extends RankingJacksonExtension<ChargeComparable, RankingChargeData>{

	public ChargeRankExtension() {
		super(ChargeComparable.class, RankingChargeData.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<ChargeComparable, RankingChargeData> entry) {
	}

	@Override
	public <P> RankingChargeData newEntryExtension(String key, P param) {
		if(param instanceof RankingChargeData){
			return (RankingChargeData)param;
		}
		ChargeInfo player = (ChargeInfo)param;
		RankingChargeData toData = new RankingChargeData();
		toData.setUserId(player.getUserId());
		return toData;
	}

}
