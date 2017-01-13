package com.rwbase.dao.groupsecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.bm.rank.RankType;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankAttribute;
import com.bm.rank.groupsecretmatch.GroupSecretMatchRankComparable;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rw.fsutil.common.SegmentList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretBaseTemplate;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretBaseCfgDAO;
import com.rwbase.dao.groupsecret.pojo.cfg.dao.GroupSecretResourceCfgDAO;
import com.rwbase.dao.groupsecret.pojo.db.GroupSecretData;

/*
 * @author HC
 * @date 2016年5月31日 下午3:58:45
 * @Description 秘境匹配的Helper
 */
public class GroupSecretMatchHelper {

	/**
	 * 更新秘境状态的回调
	 * 
	 * @author HC
	 *
	 */
	public static interface IUpdateSecretStateCallBack {
		public boolean call(GroupSecretMatchRankAttribute attr);
	}

	/**
	 * 添加秘境到排行榜中
	 * 
	 * @param player
	 * @param groupSecretData
	 */
	public static void addGroupSecret2Rank(Player player, GroupSecretData groupSecretData) {
		if (groupSecretData == null) {
			return;
		}

		String uniqueId = GroupSecretHelper.generateCacheSecretId(groupSecretData.getUserId(), groupSecretData.getId());// 唯一的Id
		Ranking<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_SECRET_MATCH_RANK);

		RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> rankingEntry = ranking.getRankingEntry(uniqueId);
		if (rankingEntry != null) {
			return;
		}

		int tFighting = 0;
		// List<Hero> maxFightingHeros = player.getHeroMgr().getMaxFightingHeros();
		List<Hero> maxFightingHeros = player.getHeroMgr().getMaxFightingHeros(player);
		for (int i = 0, size = maxFightingHeros.size(); i < size; i++) {
			Hero hero = maxFightingHeros.get(i);
			if (hero == null) {
				continue;
			}

			tFighting += hero.getFighting();
		}

		// 比较条件
		GroupSecretMatchRankComparable comparable = new GroupSecretMatchRankComparable();
		comparable.setLevel(player.getLevel());// 设置等级
		comparable.setFighting(tFighting);// 设置战斗力

		// 添加数据到排行榜
		ranking.addOrUpdateRankingEntry(uniqueId, comparable, groupSecretData);
	}

	/**
	 * 排行榜中的记录
	 * 
	 * @param player
	 * @param uniqueId
	 */
	public static void removeGroupSecretMatchEntry(Player player, String uniqueId) {
		Ranking<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_SECRET_MATCH_RANK);

		RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> rankingEntry = ranking.getRankingEntry(uniqueId);
		if (rankingEntry == null) {
			return;
		}

		ranking.removeRankingEntry(uniqueId);
	}

	/**
	 * 通知更新排行榜数据
	 * 
	 * @param uniqueId
	 * @param level
	 * @param fighting
	 */
	public static void updateRankingComparable(String uniqueId, int level, int fighting) {
		Ranking<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_SECRET_MATCH_RANK);

		RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> rankingEntry = ranking.getRankingEntry(uniqueId);
		if (rankingEntry == null) {
			return;
		}

		GroupSecretMatchRankComparable comparable = new GroupSecretMatchRankComparable();
		comparable.setLevel(level);
		comparable.setLevel(fighting);

		ranking.updateRankingEntry(rankingEntry, comparable);
	}

	/**
	 * 更新排行榜中的数据
	 * 
	 * @param uniqueId
	 * @param call 更新秘境状态的回调 {@link IUpdateSecretStateCallBack}
	 */
	public static void updateGroupSecretState(String uniqueId, IUpdateSecretStateCallBack call) {
		Ranking<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_SECRET_MATCH_RANK);

		RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> rankingEntry = ranking.getRankingEntry(uniqueId);
		if (rankingEntry == null) {
			return;
		}

		GroupSecretMatchRankAttribute attribute = rankingEntry.getExtendedAttribute();
		if (call.call(attribute)) {
			ranking.subimitUpdatedTask(rankingEntry);
		}
	}

	/**
	 * 匹配秘境
	 * 
	 * @param player
	 * @return 如果没有匹配到人的话，就返回一个Null
	 */
	public static String getGroupSecretMatchData(Player player) {
		GroupSecretBaseTemplate uniqueCfg = GroupSecretBaseCfgDAO.getCfgDAO().getUniqueCfg();
		if (uniqueCfg == null) {
			return null;
		}

		long secretCanRobMinLeftTimeMillis = TimeUnit.MINUTES.toMillis(uniqueCfg.getSecretCanRobMinLeftTime());

		GroupSecretResourceCfgDAO secretResCfgDAO = GroupSecretResourceCfgDAO.getCfgDAO();

		int level = player.getLevel();
		int fighting = 0;// 匹配的战力
		// List<Hero> maxFightingHeros = player.getHeroMgr().getMaxFightingHeros();
		List<Hero> maxFightingHeros = player.getHeroMgr().getMaxFightingHeros(player);
		for (int i = 0, size = maxFightingHeros.size(); i < size; i++) {
			Hero hero = maxFightingHeros.get(i);
			if (hero == null) {
				continue;
			}

			fighting += hero.getFighting();
		}

		int matchLowLevel = level - GroupSecretConst.MATCH_LOW_OFF_LEVEL;
		matchLowLevel = matchLowLevel <= 0 ? 1 : matchLowLevel;

		int matchUpLevel = level + GroupSecretConst.MATCH_UP_OFF_LEVEL;

		int matchLowFighting = (int) (fighting * (1 - GroupSecretConst.MATCH_LOW_OFF_FIGHTING));
		int matchUpFighting = (int) (fighting * (1 + GroupSecretConst.MATCH_UP_OFF_FIGHTING));

		// 搜索下限
		GroupSecretMatchRankComparable lowComparable = new GroupSecretMatchRankComparable();
		lowComparable.setLevel(matchLowLevel);
		lowComparable.setFighting(matchLowFighting);
		// 搜索上限
		GroupSecretMatchRankComparable upComparable = new GroupSecretMatchRankComparable();
		upComparable.setLevel(matchUpLevel);
		upComparable.setFighting(matchUpFighting);

		Ranking<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> ranking = RankingFactory.getRanking(RankType.GROUP_SECRET_MATCH_RANK);
		SegmentList<? extends MomentRankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute>> segmentList = ranking.getSegmentList(lowComparable, upComparable);
		int refSize = segmentList.getRefSize();

		long now = System.currentTimeMillis();

		String groupId = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(player.getUserId()).getGroupId();

		List<String> matchIdList = new ArrayList<String>();
		for (int i = 0; i < refSize; i++) {
			MomentRankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> momentRankingEntry = segmentList.get(i);
			if (momentRankingEntry == null) {
				continue;
			}

			if (!checkCanRob(momentRankingEntry.getExtendedAttribute(), secretResCfgDAO, secretCanRobMinLeftTimeMillis, now, groupId)) {
				continue;
			}

			matchIdList.add(momentRankingEntry.getKey());
		}

		Random r = new Random();
		if (!matchIdList.isEmpty()) {
			return matchIdList.get(r.nextInt(matchIdList.size()));
		}

		// 向上查找
		int higherRanking = ranking.higherRanking(upComparable);
		if (higherRanking != -1) {
			for (int i = higherRanking; i >= 1; --i) {
				RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> rankingEntry = ranking.getRankingEntry(i);
				if (rankingEntry == null) {
					continue;
				}

				if (!checkCanRob(rankingEntry.getExtendedAttribute(), secretResCfgDAO, secretCanRobMinLeftTimeMillis, now, groupId)) {
					continue;
				}

				return rankingEntry.getKey();
			}
		}

		// 向下查找
		int maxSize = ranking.size();
		int lowerRanking = ranking.lowerRanking(lowComparable);
		if (lowerRanking != -1) {
			for (int i = lowerRanking; i <= maxSize; i++) {
				RankingEntry<GroupSecretMatchRankComparable, GroupSecretMatchRankAttribute> rankingEntry = ranking.getRankingEntry(i);
				if (rankingEntry == null) {
					continue;
				}

				if (!checkCanRob(rankingEntry.getExtendedAttribute(), secretResCfgDAO, secretCanRobMinLeftTimeMillis, now, groupId)) {
					continue;
				}

				return rankingEntry.getKey();
			}
		}

		// 无论如何都找不到人
		return null;
	}

	/**
	 * 检查秘境能否被掠夺
	 * 
	 * @param attr
	 * @param secretResCfgDAO
	 * @param secretCanRobMinLeftTimeMillis
	 * @param now
	 * @param groupId
	 * @return
	 */
	private static boolean checkCanRob(GroupSecretMatchRankAttribute attr, GroupSecretResourceCfgDAO secretResCfgDAO, long secretCanRobMinLeftTimeMillis, long now, String groupId) {
		if (attr.getGroupId().equals(groupId)) {// 自己帮派的人是不能掠夺的
			return false;
		}

		if (!attr.isPeace()) {
			return false;
		}

		GroupSecretResourceCfg cfg = secretResCfgDAO.getGroupSecretResourceTmp(attr.getCfgId());
		if (cfg == null) {
			return false;
		}

		long createTime = attr.getCreateTime();
		long createPassTimeMillis = TimeUnit.MINUTES.toMillis(cfg.getFromCreate2RobNeedTime());
		if (now - createTime < createPassTimeMillis) {// 没有超过从创建到可以被搜索掠夺的时间点
			return false;
		}

		long needTimeMillis = TimeUnit.MINUTES.toMillis(cfg.getNeedTime());

		long leftTimeMillis = needTimeMillis - now + createTime;

		if (leftTimeMillis < secretCanRobMinLeftTimeMillis) {
			return false;
		}

		return true;
	}
}