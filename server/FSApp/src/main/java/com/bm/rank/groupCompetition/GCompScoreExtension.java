package com.bm.rank.groupCompetition;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineHurtItem;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.service.group.helper.GroupHelper;

public class GCompScoreExtension extends RankingJacksonExtension<GCompScoreComparable, GFOnlineHurtItem>{

	public GCompScoreExtension() {
		super(GCompScoreComparable.class, GFOnlineHurtItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GCompScoreComparable, GFOnlineHurtItem> entry) {
	}

	@Override
	public <P> GFOnlineHurtItem newEntryExtension(String key, P param) {
		if(param instanceof MSScoreDataItem){
			return (GFOnlineHurtItem)param;
		}
		Player player = (Player)param;
		GFOnlineHurtItem toData = new GFOnlineHurtItem();
		toData.setUserId(player.getUserId());
		toData.setGroupID(GroupHelper.getUserGroupId(player.getUserId()));
		toData.setUserName(player.getUserName());
		return toData;
	}
}
