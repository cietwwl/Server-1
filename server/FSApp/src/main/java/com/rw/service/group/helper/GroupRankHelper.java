package com.rw.service.group.helper;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.group.GroupMemberMgr;
import com.bm.rank.RankType;
import com.bm.rank.group.GroupSimpleExtAttribute;
import com.bm.rank.group.base.GroupBaseRankComparable;
import com.bm.rank.group.base.GroupBaseRankExtAttribute;
import com.bm.rank.group.createtime.GroupCreateTimeComparable;
import com.bm.rank.group.createtime.GroupCreateTimeRankExtension;
import com.bm.rank.group.membernum.GroupMemberNumComparable;
import com.bm.rank.group.membernum.GroupMemberNumRankExtension;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupBaseConfigTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupConfigCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;

/*
 * @author HC
 * @date 2016年3月5日 上午10:11:28
 * @Description 帮派排行榜Helper
 */
public class GroupRankHelper {
	private static GroupRankHelper instance = new GroupRankHelper();

	public static GroupRankHelper getInstance() {
		return instance;
	}

	protected GroupRankHelper() {
	}

	/**
	 * <pre>
	 * 帮派基础排行榜的比较Comparable {@link GroupBaseRankComparable}
	 * 帮派基础排行榜的扩展属性 {@link GroupBaseRankExtAttribute}
	 * </pre>
	 * 
	 * @param group 帮派数据
	 * @return 获取在排行榜中的排名，未入榜是-1
	 */
	public int addOrUpdateGroup2BaseRank(Group group) {
		if (group == null) {
			return -1;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return -1;
		}

		// 获取排行榜
		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_BASE_RANK);
		if (ranking == null) {
			return -1;
		}

		// 比较数据
		GroupBaseRankComparable gbrc;
		String groupId = groupData.getGroupId();
		RankingEntry rankingEntry = ranking.getRankingEntry(groupId);
		// if (rankingEntry != null) {
		// gbrc = (GroupBaseRankComparable) rankingEntry.getComparable();
		// } else {
		gbrc = new GroupBaseRankComparable();
		// }

		gbrc.setGroupLevel(groupData.getGroupLevel());
		gbrc.setGroupExp(groupData.getGroupExp());
		gbrc.setToLevelTime(groupData.getToLevelTime());

		// 加入榜
		ranking.addOrUpdateRankingEntry(groupId, gbrc, group);

		return ranking.getRanking(groupId);
	}

	/**
	 * <pre>
	 * 帮派成员排行数据的比较Comparable {@link GroupMemberNumComparable}
	 * 帮派成员排行榜的扩展属性 {@link GroupMemberNumRankExtension}
	 * </pre>
	 * 
	 * @param group 帮派数据
	 */
	public void addOrUpdateGroup2MemberNumRank(Group group) {
		if (group == null) {
			return;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return;
		}

		GroupBaseConfigTemplate gbct = GroupConfigCfgDAO.getDAO().getUniqueCfg();
		if (gbct == null) {
			return;
		}

		int memberNumRankMaxLimit = gbct.getMemberNumRankMaxLimit();

		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_MEMBER_NUM_RANK);
		if (ranking == null) {
			return;
		}

		int groupMemberSize = group.getGroupMemberMgr().getGroupMemberSize();
		// 比较数据
		GroupMemberNumComparable gmnc;
		String groupId = groupData.getGroupId();
		RankingEntry rankingEntry = ranking.getRankingEntry(groupId);
		if (rankingEntry != null) {
			// 超出上限就从榜中删除
			if (groupMemberSize > memberNumRankMaxLimit) {
				ranking.removeRankingEntry(groupId);
				return;
			}

			// gmnc = (GroupMemberNumComparable) rankingEntry.getComparable();
		} else {
			// 超出上限就从榜中删除
			if (groupMemberSize > memberNumRankMaxLimit) {
				return;
			}
			// gmnc = new GroupMemberNumComparable();
		}
		gmnc = new GroupMemberNumComparable();
		gmnc.setMemberNum(groupMemberSize);

		// 获取帮主信息
		GroupMemberDataIF leaderData = group.getGroupMemberMgr().getGroupLeader();
		ranking.addOrUpdateRankingEntry(groupId, gmnc, leaderData == null ? 0 : leaderData.getLogoutTime());
	}

	/**
	 * <pre>
	 * 帮派创建时间排行榜的比较Comparable {@link GroupCreateTimeComparable}
	 * 帮派创建时间排行榜的扩展属性 {@link GroupCreateTimeRankExtension}
	 * </pre>
	 * 
	 * @param group
	 */
	public void addGroup2CreateTimeRank(Group group) {
		if (group == null) {
			return;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return;
		}

		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_CREATE_TIME_RANK);
		if (ranking == null) {
			return;
		}

		// 比较数据
		GroupCreateTimeComparable gctc = new GroupCreateTimeComparable();
		gctc.setTime(groupData.getCreateTime());

		// 获取帮主信息
		GroupMemberDataIF leaderData = group.getGroupMemberMgr().getGroupLeader();
		ranking.addOrUpdateRankingEntry(groupData.getGroupId(), gctc, leaderData == null ? 0 : leaderData.getLogoutTime());
	}

	/**
	 * 获取帮派在基础排行榜中的排名
	 * 
	 * @param groupId
	 * @return
	 */
	public int getGroupRankIndex(String groupId) {
		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_BASE_RANK);
		if (ranking == null) {
			return -1;
		}

		return ranking.getRanking(groupId);
	}

	/**
	 * <pre>
	 * 更新帮派数据。只是更新下扩展属性
	 * 这个估计也就只有当更新帮主离线时间的时候才会用到了
	 * </pre>
	 * 
	 * @param group
	 */
	public void updateTheTypeForGroupRankExtension(String groupId) {
		if (StringUtils.isEmpty(groupId)) {
			return;
		}

		Group group = GroupBM.getInstance().get(groupId);
		if (group == null) {
			return;
		}

		final GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return;
		}

		GroupMemberMgr memberMgr = group.getGroupMemberMgr();
		updateBaseRankExtension(groupData, memberMgr);
		updateMemberCreateTimeExtension(groupData, memberMgr);
		updateMemberNumRankExtension(groupData, memberMgr);
	}

	/**
	 * 这个是更新基础排行榜中的扩展属性
	 * 
	 * @param groupData
	 * @param holder
	 */
	public void updateBaseRankExtension(GroupBaseDataIF groupData, GroupMemberMgr memberMgr) {
		if (groupData == null || memberMgr == null) {
			return;
		}

		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_BASE_RANK);
		if (ranking == null) {
			return;
		}

		String groupId = groupData.getGroupId();
		RankingEntry rankingEntry = ranking.getRankingEntry(groupId);
		if (rankingEntry == null) {
			return;
		}

		GroupBaseRankExtAttribute grea = (GroupBaseRankExtAttribute) rankingEntry.getExtendedAttribute();
		if (grea == null) {
			return;
		}

		grea.setGroupLevel(groupData.getGroupLevel());
		grea.setGroupIcon(groupData.getIconId());
		grea.setGroupName(groupData.getGroupName());
		grea.setGroupSupplies(groupData.getSupplies());
		grea.setGroupExp(groupData.getGroupExp());
		grea.setGroupMemberNum(memberMgr.getGroupMemberSize());
		// 设置帮主离线时间
		GroupMemberDataIF leaderData = memberMgr.getGroupLeader();
		grea.setLeaderLogoutTime(leaderData == null ? 0 : leaderData.getLogoutTime());
		ranking.subimitUpdatedTask(rankingEntry);
	}

	/**
	 * 这个是更新成员数量的排行榜扩展属性
	 * 
	 * @param groupData
	 * @param holder
	 */
	public void updateMemberNumRankExtension(GroupBaseDataIF groupData, GroupMemberMgr memberMgr) {
		if (groupData == null || memberMgr == null) {
			return;
		}

		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_MEMBER_NUM_RANK);
		if (ranking == null) {
			return;
		}

		String groupId = groupData.getGroupId();
		RankingEntry rankingEntry = ranking.getRankingEntry(groupId);
		if (rankingEntry == null) {
			return;
		}

		GroupSimpleExtAttribute gsea = (GroupSimpleExtAttribute) rankingEntry.getExtendedAttribute();
		if (gsea == null) {
			return;
		}

		// 设置帮主离线时间
		GroupMemberDataIF leaderData = memberMgr.getGroupLeader();
		gsea.setLeaderLogoutTime(leaderData == null ? 0 : leaderData.getLogoutTime());
		ranking.subimitUpdatedTask(rankingEntry);
	}

	/**
	 * 这个是更新帮派创建时间榜的扩展属性
	 * 
	 * @param groupData
	 * @param holder
	 */
	public void updateMemberCreateTimeExtension(GroupBaseDataIF groupData, GroupMemberMgr memberMgr) {
		if (groupData == null || memberMgr == null) {
			return;
		}

		Ranking ranking = RankingFactory.getRanking(RankType.GROUP_CREATE_TIME_RANK);
		if (ranking == null) {
			return;
		}

		String groupId = groupData.getGroupId();
		RankingEntry rankingEntry = ranking.getRankingEntry(groupId);
		if (rankingEntry == null) {
			return;
		}

		GroupSimpleExtAttribute gsea = (GroupSimpleExtAttribute) rankingEntry.getExtendedAttribute();
		if (gsea == null) {
			return;
		}

		// 设置帮主离线时间
		GroupMemberDataIF leaderData = memberMgr.getGroupLeader();
		gsea.setLeaderLogoutTime(leaderData == null ? 0 : leaderData.getLogoutTime());
		ranking.subimitUpdatedTask(rankingEntry);
	}

	/**
	 * 从排行榜中删除
	 * 
	 * @param groupId
	 */
	public void removeRanking(String groupId) {
		removeRankEntry(groupId, RankType.GROUP_BASE_RANK);// 从基础榜中移除
		removeRankEntry(groupId, RankType.GROUP_CREATE_TIME_RANK);// 从创建时间榜中移除
		removeRankEntry(groupId, RankType.GROUP_MEMBER_NUM_RANK);// 从成员数量帮中移除
		GCompFightingRankMgr.removeGroup(groupId); // 从帮派战力排行榜中删除
	}

	/**
	 * 从排行榜中移除条目
	 * 
	 * @param key
	 * @param type
	 */
	public void removeRankEntry(String key, RankType type) {
		Ranking ranking = RankingFactory.getRanking(type);
		if (ranking == null) {
			return;
		}

		ranking.removeRankingEntry(key);// 移除条目
	}
}