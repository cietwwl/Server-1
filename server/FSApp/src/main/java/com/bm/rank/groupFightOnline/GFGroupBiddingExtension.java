package com.bm.rank.groupFightOnline;

import com.bm.group.GroupBM;
import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.service.group.helper.GroupHelper;

public class GFGroupBiddingExtension extends RankingJacksonExtension<GFGroupBiddingComparable, GFGroupBiddingItem> {

	public GFGroupBiddingExtension() {
		super(GFGroupBiddingComparable.class, GFGroupBiddingItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem> entry) {
	}

	@Override
	public <P> GFGroupBiddingItem newEntryExtension(String key, P param) {
		if (param instanceof GFGroupBiddingItem) {
			return (GFGroupBiddingItem) param;
		}
		Player player = (Player) param;
		GFGroupBiddingItem toData = new GFGroupBiddingItem();
		String groupID = GroupHelper.getInstance().getUserGroupId(player.getUserId());
		toData.setGroupID(groupID);
		toData.setGroupName(GroupHelper.getInstance().getGroupName(player.getUserId()));
		String leaderName = GroupBM.getInstance().get(groupID).getGroupMemberMgr().getGroupLeader().getName();
		String iconID = GroupBM.getInstance().get(groupID).getGroupBaseDataMgr().getGroupData().getIconId();
		toData.setLeaderName(leaderName);
		toData.setIconID(iconID);
		return toData;
	}
}
