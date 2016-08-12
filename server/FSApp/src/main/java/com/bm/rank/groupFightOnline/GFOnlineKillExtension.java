package com.bm.rank.groupFightOnline;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineKillItem;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.service.group.helper.GroupHelper;

public class GFOnlineKillExtension extends RankingJacksonExtension<GFOnlineKillComparable, GFOnlineKillItem>{

	public GFOnlineKillExtension() {
		super(GFOnlineKillComparable.class, GFOnlineKillItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GFOnlineKillComparable, GFOnlineKillItem> entry) {
	}

	@Override
	public <P> GFOnlineKillItem newEntryExtension(String key, P param) {
		if(param instanceof GFOnlineKillItem){
			return (GFOnlineKillItem)param;
		}
		Player player = (Player)param;
		GFOnlineKillItem toData = new GFOnlineKillItem();
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		toData.setGroupID(GroupHelper.getUserGroupId(player.getUserId()));
		return toData;
	}
}
