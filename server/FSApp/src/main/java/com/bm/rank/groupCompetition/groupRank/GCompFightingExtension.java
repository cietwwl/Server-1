package com.bm.rank.groupCompetition.groupRank;

import com.bm.rank.RankingJacksonExtension;
import com.bm.rank.groupCompetition.scoreRank.GCompScoreComparable;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

public class GCompFightingExtension extends RankingJacksonExtension<GCompScoreComparable, GCompFightingItem>{

	public GCompFightingExtension() {
		super(GCompScoreComparable.class, GCompFightingItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GCompScoreComparable, GCompFightingItem> entry) {
	}

	@Override
	public <P> GCompFightingItem newEntryExtension(String key, P param) {
		if(param instanceof GCompFightingItem){
			return (GCompFightingItem)param;
		}
		if(param instanceof Group){
			Group group = (Group)param;
			GroupBaseDataIF groupBaseData = group.getGroupBaseDataMgr().getGroupData();
			GroupMemberDataIF leaderInfo  = group.getGroupMemberMgr().getGroupLeader();
			GCompFightingItem toData = new GCompFightingItem();
			toData.setGroupId(key);
			toData.setGroupName(groupBaseData.getGroupName());
			toData.setLeaderName(leaderInfo.getName());
			toData.setGroupIcon(groupBaseData.getIconId());
			toData.setGroupLevel(groupBaseData.getGroupLevel());
			return toData;
		}
		return null;
	}
}
