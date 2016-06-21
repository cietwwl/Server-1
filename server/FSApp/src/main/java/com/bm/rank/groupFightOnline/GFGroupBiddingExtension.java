package com.bm.rank.groupFightOnline;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.rw.fsutil.ranking.RankingEntry;

public class GFGroupBiddingExtension extends RankingJacksonExtension<GFGroupBiddingComparable, GFGroupBiddingItem>{

	public GFGroupBiddingExtension() {
		super(GFGroupBiddingComparable.class, GFGroupBiddingItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem> entry) {
	}

	@Override
	public <P> GFGroupBiddingItem newEntryExtension(String key, P param) {
		if(param instanceof GFGroupBiddingItem){
			return (GFGroupBiddingItem)param;
		}
		Player player = (Player)param;
		GFGroupBiddingItem toData = new GFGroupBiddingItem();
		toData.setGroupID(player.getGuildUserMgr().getGuildId());
		toData.setGroupName(player.getGuildUserMgr().getGuildName());
		return toData;
	}
}
