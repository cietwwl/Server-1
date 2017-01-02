package com.bm.rank.groupCompetition.winRank;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.playerdata.Player;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.group.helper.GroupHelper;

public class GCompContinueWinRankMgr {

	public static int MAX_RANK_COUNT = 50;

	public static int addOrUpdateContinueWinRank(Player player, int currentContinueWin) {
		if (currentContinueWin <= 0) {
			return -1;
		}
		Ranking<GCompContinueWinComparable, GCompContinueWinItem> ranking = RankingFactory.getRanking(RankType.GCOMP_CONTINUE_WIN_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		GCompContinueWinComparable comparable = new GCompContinueWinComparable(currentContinueWin, System.currentTimeMillis());
		String userID = player.getUserId();
		RankingEntry<GCompContinueWinComparable, GCompContinueWinItem> rankingEntry = ranking.getRankingEntry(userID);
		if (rankingEntry == null) {
			// 加入榜
			ranking.addOrUpdateRankingEntry(userID, comparable, player);
		} else {
			int oldMax = rankingEntry.getComparable().getContinueWin();
			if (oldMax >= currentContinueWin) {
				return -2;
			}
			// 更新榜
			ranking.updateRankingEntry(rankingEntry, comparable);
		}
		return ranking.getRanking(userID);
	}

	/**
	 * 最高连胜排名
	 * 
	 * @param resourceID
	 * @param userID
	 * @return
	 */
	public static int getRankIndex(String userID) {
		Ranking<GCompContinueWinComparable, GCompContinueWinItem> ranking = RankingFactory.getRanking(RankType.GCOMP_CONTINUE_WIN_RANK);
		if (ranking == null) {
			return -1;
		}
		return ranking.getRanking(userID);
	}

	public static List<GCompContinueWinItem> getContinueWinRankList() {
		List<GCompContinueWinItem> itemList = new ArrayList<GCompContinueWinItem>();
		Ranking<GCompContinueWinComparable, GCompContinueWinItem> ranking = RankingFactory.getRanking(RankType.GCOMP_CONTINUE_WIN_RANK);
		EnumerateList<? extends MomentRankingEntry<GCompContinueWinComparable, GCompContinueWinItem>> it = ranking.getEntriesEnumeration(1, MAX_RANK_COUNT);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GCompContinueWinComparable, GCompContinueWinItem> entry = it.nextElement();
			GCompContinueWinComparable continueWinComparable = entry.getComparable();
			GCompContinueWinItem winItem = entry.getExtendedAttribute();
			winItem.setContinueWin(continueWinComparable.getContinueWin());
			itemList.add(winItem);
		}
		return itemList;
	}

	public static void clearRank() {
		Ranking<GCompContinueWinComparable, GCompContinueWinItem> ranking = RankingFactory.getRanking(RankType.GCOMP_CONTINUE_WIN_RANK);
		ranking.clear();
	}

	/**
	 * 更新玩家的在排行榜中的基本信息（不是排行信息） 例如：名字的修改，帮派名字的修改，头像框的修改
	 * 
	 * @param player
	 */
	public static void updateContinueWinRankBaseInfo(Player player) {
		// TODO 这里可能需要从排行榜中删除(如果玩家过程中没有了帮派)
		Ranking<GCompContinueWinComparable, GCompContinueWinItem> ranking = RankingFactory.getRanking(RankType.GCOMP_CONTINUE_WIN_RANK);
		RankingEntry<GCompContinueWinComparable, GCompContinueWinItem> entry = ranking.getRankingEntry(player.getUserId());
		if (entry != null) {
			String groupName = GroupHelper.getInstance().getGroupName(player.getUserId());
			if (StringUtils.isBlank(groupName)) {
				ranking.removeRankingEntry(player.getUserId());
				return;
			}
			entry.getExtendedAttribute().setUserName(player.getUserName());
			entry.getExtendedAttribute().setHeadImage(player.getHeadImage());
			entry.getExtendedAttribute().setGroupName(GroupHelper.getInstance().getGroupName(player.getUserId()));
			ranking.subimitUpdatedTask(entry);
		}
	}

	/**
	 * 阶段结束的时候，保存排行榜数据，并清空排行榜
	 * 
	 * @return
	 */
	public static List<GCompContinueWinItem> stageEnd() {
		List<GCompContinueWinItem> needKeepRank = getContinueWinRankList();
		clearRank();
		return needKeepRank;
	}
}