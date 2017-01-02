package com.bm.rank.groupFightOnline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.cfg.GFightOnlineDefeatRankCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineDefeatRankDAO;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFFinalRewardItem;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineKillItem;
import com.playerdata.groupFightOnline.enums.GFRewardType;
import com.playerdata.groupFightOnline.manager.GFFinalRewardMgr;
import com.rw.fsutil.common.EnumerateList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.dao.email.EmailCfg;
import com.rwbase.dao.email.EmailCfgDAO;

public class GFOnlineKillRankMgr {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdateUserGFKillRank(Player player, UserGFightOnlineData userGFInfo) {
		Ranking ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		GFOnlineKillComparable comparable = new GFOnlineKillComparable(userGFInfo.getResourceID(), userGFInfo.getKillCount(), System.currentTimeMillis());
		String userID = userGFInfo.getId();
		RankingEntry<GFOnlineKillComparable, GFOnlineKillItem> rankingEntry = ranking.getRankingEntry(userID);
		if (rankingEntry == null) {
			// 加入榜
			ranking.addOrUpdateRankingEntry(userID, comparable, player);
		} else {
			// 更新榜
			ranking.updateRankingEntry(rankingEntry, comparable);
		}
		return ranking.getRanking(userID);
	}

	/**
	 * 资源点中的杀敌数排名
	 * 
	 * @param resourceID
	 * @param groupID
	 * @return
	 */
	public static int getRankIndex(int resourceID, String userID) {
		List<GFOnlineKillItem> itemList = new ArrayList<GFOnlineKillItem>();
		GFOnlineKillItem target = null;
		Ranking<GFOnlineKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		EnumerateList<? extends MomentRankingEntry<GFOnlineKillComparable, GFOnlineKillItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFOnlineKillComparable, GFOnlineKillItem> entry = it.nextElement();
			GFOnlineKillComparable killComparable = entry.getComparable();
			if (killComparable.getResourceID() != resourceID)
				continue;
			GFOnlineKillItem killItem = entry.getExtendedAttribute();
			if (killItem.getUserId().equals(userID))
				target = killItem;
			itemList.add(killItem);
		}
		int indx = itemList.indexOf(target);
		return indx >= 0 ? indx + 1 : -1;
	}

	public static List<GFOnlineKillItem> getGFKillRankList(int resourceID) {
		List<GFOnlineKillItem> itemList = new ArrayList<GFOnlineKillItem>();
		Ranking<GFOnlineKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		EnumerateList<? extends MomentRankingEntry<GFOnlineKillComparable, GFOnlineKillItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFOnlineKillComparable, GFOnlineKillItem> entry = it.nextElement();
			GFOnlineKillComparable killComparable = entry.getComparable();
			if (killComparable.getResourceID() != resourceID)
				continue;
			GFOnlineKillItem killItem = entry.getExtendedAttribute();
			killItem.setTotalKill(killComparable.getTotalKill());
			itemList.add(killItem);
		}
		return itemList;
	}

	public static List<GFOnlineKillItem> getGFKillRankListInGroup(int resourceID, String groupID, int size) {
		List<GFOnlineKillItem> result = new ArrayList<GFOnlineKillItem>();

		Ranking<GFOnlineKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		EnumerateList<? extends MomentRankingEntry<GFOnlineKillComparable, GFOnlineKillItem>> it = ranking.getEntriesEnumeration(1, size);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFOnlineKillComparable, GFOnlineKillItem> entry = it.nextElement();
			GFOnlineKillComparable killComparable = entry.getComparable();
			if (killComparable.getResourceID() != resourceID)
				continue;
			GFOnlineKillItem killItem = entry.getExtendedAttribute();
			if (StringUtils.equals(killItem.getGroupID(), groupID)) {
				killItem.setTotalKill(killComparable.getTotalKill());
				result.add(killItem);
			}
		}
		return result;
	}

	public static void dispatchKillReward(int resourceID) {
		int dispatchingRank = 0; // 记录正在发放奖励的排名，用做异常的时候查找出错点
		String dispatchingUser = "0"; // 记录正在发放奖励的角色id，用做异常的时候查找出错点
		long currentTime = System.currentTimeMillis(); // 记录奖励发放的时间

		GFightOnlineResourceCfg resCfg = GFightOnlineResourceCfgDAO.getInstance().getCfgById(String.valueOf(resourceID));
		if (resCfg == null)
			return;
		try {
			List<GFOnlineKillItem> killRank = getGFKillRankList(resourceID);
			Iterator<GFOnlineKillItem> it = killRank.iterator();
			GFightOnlineDefeatRankDAO defeatRankDAO = GFightOnlineDefeatRankDAO.getInstance();
			int rewardCfgCount = defeatRankDAO.getEntryCount();
			for (int i = 1; i <= rewardCfgCount; i++) {
				int startRank = 1;
				if (i != 1)
					startRank = defeatRankDAO.getCfgById(String.valueOf(i - 1)).getRankEnd() + 1;
				GFightOnlineDefeatRankCfg rewardCfg = defeatRankDAO.getCfgById(String.valueOf(i));
				int endRank = rewardCfg.getRankEnd();
				for (int j = startRank; j <= endRank; j++) {
					dispatchingRank = j;
					if (it.hasNext()) {
						GFOnlineKillItem entry = it.next();
						dispatchingUser = entry.getUserId();
						// 构造奖励内容
						GFFinalRewardItem finalRewardItem = new GFFinalRewardItem();
						finalRewardItem.setEmailId(rewardCfg.getEmailId());
						EmailCfg emailCfg = EmailCfgDAO.getInstance().getCfgById(String.valueOf(rewardCfg.getEmailId()));
						if (emailCfg != null) {
							finalRewardItem.setRewardDesc(String.format(emailCfg.getContent(), resCfg.getResName(), entry.getTotalKill(), j));
							finalRewardItem.setEmailIconPath(emailCfg.getSubjectIcon());
						}

						finalRewardItem.setResourceID(resourceID);
						finalRewardItem.setRewardContent(rewardCfg.getRewardList());
						finalRewardItem.setRewardGetTime(currentTime);
						finalRewardItem.setRewardID(GFFinalRewardMgr.getInstance().getRewardID(dispatchingUser, resourceID, GFRewardType.KillRankReward));
						finalRewardItem.setRewardOwner(GFFinalRewardMgr.getInstance().getOwnerID(dispatchingUser, resourceID));
						finalRewardItem.setRewardType(GFRewardType.KillRankReward.getValue());
						finalRewardItem.setUserID(dispatchingUser);
						GFFinalRewardMgr.getInstance().addGFReward(dispatchingUser, resourceID, finalRewardItem);
					}
				}
			}
		} catch (Exception ex) {
			GameLog.error(LogModule.GroupFightOnline, "GFOnlineKillRankMgr", String.format("dispatchKillReward, 给角色[%s]发放帮战杀敌数排行[%s]奖励的时候出现异常", dispatchingUser, dispatchingRank), ex);
		} finally {
			clearRank(resourceID);
		}
	}

	public static void clearRank(int resourceID) {
		List<GFOnlineKillItem> itemList = getGFKillRankList(resourceID);
		Ranking<GFOnlineKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		for (GFOnlineKillItem removeItem : itemList) {
			ranking.removeRankingEntry(removeItem.getUserId());
		}
	}

	public static void updateGFKillRankInfo(Player player) {
		Ranking<GFOnlineKillComparable, GFOnlineKillItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_KILL_RANK);
		RankingEntry<GFOnlineKillComparable, GFOnlineKillItem> entry = ranking.getRankingEntry(player.getUserId());
		if (entry != null) {
			entry.getExtendedAttribute().setUserName(player.getUserName());
			entry.getExtendedAttribute().setGroupID(GroupHelper.getInstance().getUserGroupId(player.getUserId()));
			ranking.subimitUpdatedTask(entry);
		}
	}
}