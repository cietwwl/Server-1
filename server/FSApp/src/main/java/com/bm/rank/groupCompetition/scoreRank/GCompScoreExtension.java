package com.bm.rank.groupCompetition.scoreRank;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.service.group.helper.GroupHelper;

public class GCompScoreExtension extends RankingJacksonExtension<GCompScoreComparable, GCompScoreItem> {

	public GCompScoreExtension() {
		super(GCompScoreComparable.class, GCompScoreItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GCompScoreComparable, GCompScoreItem> entry) {
	}

	@Override
	public <P> GCompScoreItem newEntryExtension(String key, P param) {
		if (param instanceof GCompScoreItem) {
			return (GCompScoreItem) param;
		}
		Player player = (Player) param;
		GCompScoreItem toData = new GCompScoreItem();
		toData.setUserId(player.getUserId());
		toData.setUserName(player.getUserName());
		toData.setGroupName(GroupHelper.getInstance().getGroupName(player.getUserId()));
		toData.setHeadImage(player.getHeadImage());
		return toData;
	}
}
