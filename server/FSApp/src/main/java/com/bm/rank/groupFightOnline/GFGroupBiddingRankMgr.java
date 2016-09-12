package com.bm.rank.groupFightOnline;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.group.GroupBM;
import com.bm.rank.RankType;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.bm.GFightConst;
import com.playerdata.groupFightOnline.data.GFightOnlineGroupData;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.group.pojo.Group;

public class GFGroupBiddingRankMgr {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdateGFGroupBidRank(Player player, GFightOnlineGroupData bidInfo) {
		Ranking ranking = RankingFactory.getRanking(RankType.GF_ONLINE_GROUP_BID_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		GFGroupBiddingComparable comparable = new GFGroupBiddingComparable(bidInfo.getResourceID(), bidInfo.getBiddingCount(), System.currentTimeMillis());
		String groupID = bidInfo.getGroupID();
		RankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem> rankingEntry = ranking.getRankingEntry(groupID);
		if (rankingEntry == null) {
			// 加入榜
			ranking.addOrUpdateRankingEntry(groupID, comparable, player);
		} else {
			// 更新榜
			ranking.updateRankingEntry(rankingEntry, comparable);
		}
		return ranking.getRanking(groupID);
	}

	/**
	 * 资源点中的帮派竞标排名
	 * @param resourceID
	 * @param groupID
	 * @return
	 */
	public static int getRankIndex(int resourceID, String groupID) {
		List<GFGroupBiddingItem> itemList = new ArrayList<GFGroupBiddingItem>();
		GFGroupBiddingItem target = null;
		Ranking<GFGroupBiddingComparable, GFGroupBiddingItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_GROUP_BID_RANK);
		EnumerateList<? extends MomentRankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem> entry = it.nextElement();
			GFGroupBiddingComparable bidComparable = entry.getComparable();
			if(bidComparable.getResourceID() != resourceID) continue;
			GFGroupBiddingItem bidItem = entry.getExtendedAttribute();
			if(StringUtils.equals(bidItem.getGroupID(), groupID)) target = bidItem;
			itemList.add(bidItem);
		}
		int indx = -1;
		if(null != target) indx = itemList.indexOf(target);
		return indx >= 0 ? indx + 1 : -1;
	}

	public static List<GFGroupBiddingItem> getGFGroupBidRankList(int resourceID) {
		List<GFGroupBiddingItem> itemList = new ArrayList<GFGroupBiddingItem>();
		Ranking<GFGroupBiddingComparable, GFGroupBiddingItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_GROUP_BID_RANK);
		EnumerateList<? extends MomentRankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem> entry = it.nextElement();
			GFGroupBiddingComparable bidComparable = entry.getComparable();
			if(bidComparable.getResourceID() != resourceID) continue;
			GFGroupBiddingItem bidItem = entry.getExtendedAttribute();
			bidItem.setTotalBidding(bidComparable.getTotalBid());
			Group group = GroupBM.get(bidItem.getGroupID());
			if(group != null){
				bidItem.setGroupName(group.getGroupBaseDataMgr().getGroupData().getGroupName());
			}
			itemList.add(bidItem);
			if(itemList.size() > GFightConst.GROUP_BID_RANK_COUNT) break;
		}
		return itemList;
	}
	
	public static List<GFGroupBiddingItem> getGFGroupBidRankList(int resourceID, int maxSize) {
		List<GFGroupBiddingItem> itemList = new ArrayList<GFGroupBiddingItem>();
		Ranking<GFGroupBiddingComparable, GFGroupBiddingItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_GROUP_BID_RANK);
		EnumerateList<? extends MomentRankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem>> it = ranking.getEntriesEnumeration(1, maxSize);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem> entry = it.nextElement();
			GFGroupBiddingComparable bidComparable = entry.getComparable();
			if(bidComparable.getResourceID() != resourceID) continue;
			GFGroupBiddingItem bidItem = entry.getExtendedAttribute();
			bidItem.setTotalBidding(bidComparable.getTotalBid());
			Group group = GroupBM.get(bidItem.getGroupID());
			if(group != null){
				bidItem.setGroupName(group.getGroupBaseDataMgr().getGroupData().getGroupName());
			}
			itemList.add(bidItem);
		}
		return itemList;
	}
	
	public static void clearRank(int resourceID){
		List<GFGroupBiddingItem> itemList = getGFGroupBidRankList(resourceID);
		Ranking<GFGroupBiddingComparable, GFGroupBiddingItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_GROUP_BID_RANK);
		for(GFGroupBiddingItem removeItem : itemList){
			ranking.removeRankingEntry(removeItem.getGroupID());
		}
	}
	
	public static void updateGFBidRankInfo(String groupID){
		Group gp = GroupBM.get(groupID);
		if(gp == null) return;
		Ranking<GFGroupBiddingComparable, GFGroupBiddingItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_GROUP_BID_RANK);
		RankingEntry<GFGroupBiddingComparable, GFGroupBiddingItem> entry = ranking.getRankingEntry(groupID);
		if(entry == null) return;
		entry.getExtendedAttribute().setGroupName(gp.getGroupBaseDataMgr().getGroupData().getGroupName());
		entry.getExtendedAttribute().setIconID(gp.getGroupBaseDataMgr().getGroupData().getIconId());
		entry.getExtendedAttribute().setLeaderName(gp.getGroupMemberMgr().getGroupLeader().getName());
		ranking.subimitUpdatedTask(entry);
	}
}