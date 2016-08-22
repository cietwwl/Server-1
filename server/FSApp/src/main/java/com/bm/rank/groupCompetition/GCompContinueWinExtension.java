package com.bm.rank.groupCompetition;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.service.group.helper.GroupHelper;

public class GCompContinueWinExtension extends RankingJacksonExtension<GCompScoreComparable, GCompContinueWinItem>{

	public GCompContinueWinExtension() {
		super(GCompScoreComparable.class, GCompContinueWinItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GCompScoreComparable, GCompContinueWinItem> entry) {
	}

	@Override
	public <P> GCompContinueWinItem newEntryExtension(String key, P param) {
		if(param instanceof GCompContinueWinItem){
			return (GCompContinueWinItem)param;
		}
		Player player = (Player)param;
		GCompContinueWinItem toData = new GCompContinueWinItem();
		toData.setUserId(player.getUserId());
		toData.setGroupName(GroupHelper.getGroupName(player.getUserId()));
		toData.setUserName(player.getUserName());
		return toData;
	}
}
