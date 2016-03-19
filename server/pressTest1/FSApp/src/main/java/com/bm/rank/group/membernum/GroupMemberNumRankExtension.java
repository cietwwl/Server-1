package com.bm.rank.group.membernum;

import com.bm.rank.RankingJacksonExtension;
import com.bm.rank.group.GroupSimpleExtAttribute;
import com.rw.fsutil.ranking.RankingEntry;

/*
 * @author HC
 * @date 2016年1月20日 上午10:46:29
 * @Description 帮派成员人数排行榜
 */
public class GroupMemberNumRankExtension extends RankingJacksonExtension<GroupMemberNumComparable, GroupSimpleExtAttribute> {

	public GroupMemberNumRankExtension() {
		super(GroupMemberNumComparable.class, GroupSimpleExtAttribute.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GroupMemberNumComparable, GroupSimpleExtAttribute> entry) {
	}

	/**
	 * 传进来的P应该是帮主登出游戏的时间
	 */
	@Override
	public <P> GroupSimpleExtAttribute newEntryExtension(String key, P customParam) {
		long logoutTime = (Long) customParam;

		GroupSimpleExtAttribute gsea = new GroupSimpleExtAttribute();
		gsea.setGroupId(key);
		gsea.setLeaderLogoutTime(logoutTime);
		return gsea;
	}
}