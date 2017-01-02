package com.bm.rank.groupFightOnline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.rank.RankType;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.groupFightOnline.cfg.GFightOnlineDamageRankCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineDamageRankDAO;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfg;
import com.playerdata.groupFightOnline.cfg.GFightOnlineResourceCfgDAO;
import com.playerdata.groupFightOnline.data.GFFinalRewardItem;
import com.playerdata.groupFightOnline.data.UserGFightOnlineData;
import com.playerdata.groupFightOnline.dataForRank.GFOnlineHurtItem;
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

public class GFOnlineHurtRankMgr {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int addOrUpdateUserGFHurtRank(Player player, UserGFightOnlineData userGFInfo) {
		Ranking ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		if (ranking == null) {
			return -1;
		}
		// 比较数据
		GFOnlineHurtComparable comparable = new GFOnlineHurtComparable(userGFInfo.getResourceID(), userGFInfo.getHurtTotal(), System.currentTimeMillis());
		String userID = userGFInfo.getId();
		RankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem> rankingEntry = ranking.getRankingEntry(userID);
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
	 * 资源点中的伤害排名
	 * 
	 * @param resourceID
	 * @param userID
	 * @return
	 */
	public static int getRankIndex(int resourceID, String userID) {
		List<GFOnlineHurtItem> itemList = new ArrayList<GFOnlineHurtItem>();
		GFOnlineHurtItem target = null;
		Ranking<GFOnlineHurtComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem> entry = it.nextElement();
			GFOnlineHurtComparable hurtComparable = entry.getComparable();
			if (hurtComparable.getResourceID() != resourceID)
				continue;
			GFOnlineHurtItem hurtItem = entry.getExtendedAttribute();
			if (hurtItem.getUserId().equals(userID))
				target = hurtItem;
			itemList.add(hurtItem);
		}
		int indx = itemList.indexOf(target);
		return indx >= 0 ? indx + 1 : -1;
	}

	public static List<GFOnlineHurtItem> getGFHurtRankList(int resourceID) {
		List<GFOnlineHurtItem> itemList = new ArrayList<GFOnlineHurtItem>();
		Ranking<GFOnlineHurtComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem>> it = ranking.getEntriesEnumeration();
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem> entry = it.nextElement();
			GFOnlineHurtComparable hurtComparable = entry.getComparable();
			if (hurtComparable.getResourceID() != resourceID)
				continue;
			GFOnlineHurtItem hurtItem = entry.getExtendedAttribute();
			hurtItem.setTotalHurt(hurtComparable.getTotalHurt());
			itemList.add(hurtItem);
		}
		return itemList;
	}

	public static List<GFOnlineHurtItem> getGFHurtRankListInGroup(int resourceID, String groupID, int size) {
		List<GFOnlineHurtItem> result = new ArrayList<GFOnlineHurtItem>();
		Ranking<GFOnlineHurtComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		EnumerateList<? extends MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem>> it = ranking.getEntriesEnumeration(1, size);
		for (; it.hasMoreElements();) {
			MomentRankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem> entry = it.nextElement();
			GFOnlineHurtComparable hurtComparable = entry.getComparable();
			if (hurtComparable.getResourceID() != resourceID)
				continue;
			GFOnlineHurtItem hurtItem = entry.getExtendedAttribute();
			if (StringUtils.equals(hurtItem.getGroupID(), groupID)) {
				hurtItem.setTotalHurt(hurtComparable.getTotalHurt());
				result.add(hurtItem);
			}
		}
		return result;
	}

	public static void dispatchHurtReward(int resourceID) {
		int dispatchingRank = 0; // 记录正在发放奖励的排名，用做异常的时候查找出错点
		String dispatchingUser = "0"; // 记录正在发放奖励的角色id，用做异常的时候查找出错点
		long currentTime = System.currentTimeMillis(); // 记录奖励发放的时间

		GFightOnlineResourceCfg resCfg = GFightOnlineResourceCfgDAO.getInstance().getCfgById(String.valueOf(resourceID));
		if (resCfg == null)
			return;
		try {
			List<GFOnlineHurtItem> hurtRank = getGFHurtRankList(resourceID);
			Iterator<GFOnlineHurtItem> it = hurtRank.iterator();

			GFightOnlineDamageRankDAO damageRankDAO = GFightOnlineDamageRankDAO.getInstance();
			int rewardCfgCount = damageRankDAO.getEntryCount();
			for (int i = 1; i <= rewardCfgCount; i++) {
				int startRank = 1;
				if (i != 1)
					startRank = damageRankDAO.getCfgById(String.valueOf(i - 1)).getRankEnd() + 1;
				GFightOnlineDamageRankCfg rewardCfg = damageRankDAO.getCfgById(String.valueOf(i));
				int endRank = rewardCfg.getRankEnd();
				for (int j = startRank; j <= endRank; j++) {
					dispatchingRank = j;
					if (it.hasNext()) {
						GFOnlineHurtItem entry = it.next();
						dispatchingUser = entry.getUserId();
						// 构造奖励内容
						GFFinalRewardItem finalRewardItem = new GFFinalRewardItem();
						finalRewardItem.setEmailId(rewardCfg.getEmailId());

						EmailCfg emailCfg = EmailCfgDAO.getInstance().getCfgById(String.valueOf(rewardCfg.getEmailId()));
						if (emailCfg != null) {
							finalRewardItem.setRewardDesc(String.format(emailCfg.getContent(), resCfg.getResName(), entry.getTotalHurt(), j));
							finalRewardItem.setEmailIconPath(emailCfg.getSubjectIcon());
						}

						finalRewardItem.setResourceID(resourceID);
						finalRewardItem.setRewardContent(rewardCfg.getRewardList());
						finalRewardItem.setRewardGetTime(currentTime);
						finalRewardItem.setRewardID(GFFinalRewardMgr.getInstance().getRewardID(dispatchingUser, resourceID, GFRewardType.HurtRankReward));
						finalRewardItem.setRewardOwner(GFFinalRewardMgr.getInstance().getOwnerID(dispatchingUser, resourceID));
						finalRewardItem.setRewardType(GFRewardType.HurtRankReward.getValue());
						finalRewardItem.setUserID(dispatchingUser);
						GFFinalRewardMgr.getInstance().addGFReward(dispatchingUser, resourceID, finalRewardItem);
					}
				}
			}
		} catch (Exception ex) {
			GameLog.error(LogModule.GroupFightOnline, "GFOnlineHurtRankMgr", String.format("dispatchHurtReward, 给角色[%s]发放帮战伤害排行[%s]奖励的时候出现异常", dispatchingUser, dispatchingRank), ex);
		} finally {
			clearRank(resourceID);
		}
	}

	public static void clearRank(int resourceID) {
		List<GFOnlineHurtItem> itemList = getGFHurtRankList(resourceID);
		Ranking<GFOnlineHurtComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		for (GFOnlineHurtItem removeItem : itemList) {
			ranking.removeRankingEntry(removeItem.getUserId());
		}
	}

	public static void updateGFHurtRankInfo(Player player) {
		Ranking<GFOnlineHurtComparable, GFOnlineHurtItem> ranking = RankingFactory.getRanking(RankType.GF_ONLINE_HURT_RANK);
		RankingEntry<GFOnlineHurtComparable, GFOnlineHurtItem> entry = ranking.getRankingEntry(player.getUserId());
		if (entry != null) {
			entry.getExtendedAttribute().setUserName(player.getUserName());
			entry.getExtendedAttribute().setGroupID(GroupHelper.getInstance().getUserGroupId(player.getUserId()));
			ranking.subimitUpdatedTask(entry);
		}
	}
}