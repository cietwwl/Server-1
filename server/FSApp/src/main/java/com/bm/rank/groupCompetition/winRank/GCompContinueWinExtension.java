package com.bm.rank.groupCompetition.winRank;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.service.group.helper.GroupHelper;

public class GCompContinueWinExtension extends RankingJacksonExtension<GCompContinueWinComparable, GCompContinueWinItem> {

	public GCompContinueWinExtension() {
		super(GCompContinueWinComparable.class, GCompContinueWinItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GCompContinueWinComparable, GCompContinueWinItem> entry) {
	}

	@Override
	public <P> GCompContinueWinItem newEntryExtension(String key, P param) {
		if (param instanceof GCompContinueWinItem) {
			return (GCompContinueWinItem) param;
		}
		Player player = (Player) param;
		GCompContinueWinItem toData = new GCompContinueWinItem();
		toData.setUserId(player.getUserId());
		toData.setGroupName(GroupHelper.getInstance().getGroupName(player.getUserId()));
		toData.setUserName(player.getUserName());
		toData.setHeadImage(player.getHeadImage());
		return toData;
	}
}
