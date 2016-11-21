package com.bm.rank.recharge;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
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
		Player player = (Player)param;
		RankingChargeData toData = new RankingChargeData();
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		return toData;
	}
}
