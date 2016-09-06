package com.bm.worldBoss.rank;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.rw.fsutil.ranking.RankingEntry;

public class WBHurtExtension extends RankingJacksonExtension<WBHurtComparable, WBHurtItem>{

	public WBHurtExtension() {
		super(WBHurtComparable.class, WBHurtItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<WBHurtComparable, WBHurtItem> entry) {
	}

	@Override
	public <P> WBHurtItem newEntryExtension(String key, P param) {
		if(param instanceof MSScoreDataItem){
			return (WBHurtItem)param;
		}
		Player player = (Player)param;
		WBHurtItem toData = new WBHurtItem();
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		return toData;
	}
}
