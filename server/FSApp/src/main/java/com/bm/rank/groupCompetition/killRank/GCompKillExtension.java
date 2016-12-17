package com.bm.rank.groupCompetition.killRank;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.service.group.helper.GroupHelper;

public class GCompKillExtension extends RankingJacksonExtension<GCompKillComparable, GCompKillItem> {

	public GCompKillExtension() {
		super(GCompKillComparable.class, GCompKillItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GCompKillComparable, GCompKillItem> entry) {
	}

	@Override
	public <P> GCompKillItem newEntryExtension(String key, P param) {
		if (param instanceof GCompKillItem) {
			return (GCompKillItem) param;
		}
		Player player = (Player) param;
		GCompKillItem toData = new GCompKillItem();
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		toData.setGroupName(GroupHelper.getInstance().getGroupName(player.getUserId()));
		toData.setHeadImage(player.getHeadImage());
		return toData;
	}
}
