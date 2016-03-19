package com.bm.rank.group.createtime;

import com.bm.rank.RankingJacksonExtension;
import com.bm.rank.group.GroupSimpleExtAttribute;
import com.rw.fsutil.ranking.RankingEntry;

/*
 * @author HC
 * @date 2016年1月20日 上午10:26:33
 * @Description 排行榜中的简单数据
 */
public class GroupCreateTimeRankExtension extends RankingJacksonExtension<GroupCreateTimeComparable, GroupSimpleExtAttribute> {

	public GroupCreateTimeRankExtension() {
		super(GroupCreateTimeComparable.class, GroupSimpleExtAttribute.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GroupCreateTimeComparable, GroupSimpleExtAttribute> entry) {
	}

	/**
	 * 传递进来的P应该是帮主登出游戏的时间
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