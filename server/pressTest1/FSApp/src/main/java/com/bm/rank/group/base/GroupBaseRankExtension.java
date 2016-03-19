package com.bm.rank.group.base;

import com.bm.group.GroupMemberMgr;
import com.bm.rank.RankingJacksonExtension;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

/*
 * @author HC
 * @date 2016年1月19日 下午8:16:51
 * @Description 帮派基础排行榜
 */
public class GroupBaseRankExtension extends RankingJacksonExtension<GroupBaseRankComparable, GroupBaseRankExtAttribute> {

	public GroupBaseRankExtension() {
		super(GroupBaseRankComparable.class, GroupBaseRankExtAttribute.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GroupBaseRankComparable, GroupBaseRankExtAttribute> entry) {
	}

	/**
	 * 对于此方法我要传递进来一个Group的对象
	 */
	@Override
	public <P> GroupBaseRankExtAttribute newEntryExtension(String key, P customParam) {
		Group group = (Group) customParam;
		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		GroupMemberMgr memberMgr = group.getGroupMemberMgr();

		GroupBaseRankExtAttribute grea = new GroupBaseRankExtAttribute();
		grea.setGroupId(groupData.getGroupId());
		grea.setGroupLevel(groupData.getGroupLevel());
		grea.setGroupIcon(groupData.getIconId());
		grea.setGroupName(groupData.getGroupName());
		grea.setGroupSupplies(groupData.getSupplies());
		grea.setGroupExp(groupData.getGroupExp());
		grea.setGroupMemberNum(memberMgr == null ? 0 : memberMgr.getGroupMemberSize());
		// 设置帮主离线时间
		GroupMemberDataIF leaderData = memberMgr.getGroupLeader();
		grea.setLeaderLogoutTime(leaderData == null ? 0 : leaderData.getLogoutTime());
		return grea;
	}
}