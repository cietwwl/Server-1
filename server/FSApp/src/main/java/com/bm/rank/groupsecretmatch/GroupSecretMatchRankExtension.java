package com.bm.rank.groupsecretmatch;

import com.bm.rank.RankingJacksonExtension;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;

/*
 * @author HC
 * @date 2016年5月26日 下午5:55:20
 * @Description 帮派秘境匹配的排行榜
 */
public class GroupSecretMatchRankExtension extends RankingJacksonExtension<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> {

	public GroupSecretMatchRankExtension() {
		super(GroupSecretMatchRankComparable.class, GroupSecretMatchRankAttribute.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> entry) {
	}

	@Override
	public <P> GroupSecretMatchRankAttribute newEntryExtension(String key, P customParam) {
		GroupSecretData groupSecretData = (GroupSecretData) customParam;
		// 扩展属性
		GroupSecretMatchRankAttribute attr = new GroupSecretMatchRankAttribute(groupSecretData.getCreateTime(), groupSecretData.getSecretId(), groupSecretData.getGroupId());
		return attr;
	}
}