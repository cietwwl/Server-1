package com.rwbase.dao.anglearray.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.arena.ArenaBM;
import com.bm.rank.RankType;
import com.bm.rank.anglearray.AngleArrayAttribute;
import com.bm.rank.anglearray.AngleArrayComparable;
import com.bm.rank.fightingAll.FightingComparable;
import com.common.Weight;
import com.log.GameLog;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyInfoHelper;
import com.playerdata.army.CurAttrData;
import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.common.SegmentList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.anglearray.AngelArrayConst;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

/*
 * @author HC
 * @date 2015年11月13日 下午3:47:37
 * @Description 万仙阵匹配的处理
 */
public final class AngleArrayMatchHelper {

	/**
	 * 获取匹配到的阵容信息
	 * 
	 * @param userId 角色Id
	 * @param level 角色等级
	 * @param maxLevel 最大等级
	 * @param minFighting 最小战力
	 * @param maxFighting 最大战力
	 * @param enemyIdList 已经匹配到的人的Id列表
	 * @return
	 */
	public static ArmyInfo getMatchArmyInfo(String userId, int level, int maxLevel, int minFighting, int maxFighting, List<String> enemyIdList) {
		// 第一步等级匹配 && 第二步战力匹配
		List<String> matchUsetIdList = getMatchUsetIdList(userId, level, maxLevel, minFighting, maxFighting, enemyIdList);

		// 检查是否达到了最低保底数量，没有就通过战力榜去匹配到20个
		int matchSize = matchUsetIdList.size();
		int needFightingRankCount = AngelArrayConst.MIN_MATCH_SIZE - matchSize;
		if (needFightingRankCount > 0) {
			getFightingRankMatch(userId, needFightingRankCount, minFighting, maxFighting, matchUsetIdList, enemyIdList);
		}

		// 如果没有匹配到任何人
		if (matchUsetIdList.isEmpty()) {
			GameLog.error("万仙阵匹配", userId, String.format("等级下限[%s]，上[%s]，战力下[%s]，上[%s]，没有从排行榜随机到人", level, maxLevel, minFighting, maxFighting));
			return null;
		}

		// 第三步找出来的人进行排名权重
		// 第四步竞技活跃权重
		long now = System.currentTimeMillis();
		Map<String, Integer> proMap = new HashMap<String, Integer>();
		for (int i = 0, size = matchUsetIdList.size(); i < size; i++) {
			String matchUserId = matchUsetIdList.get(i);
			TableArenaData arenaData = ArenaBM.getInstance().getArenaData(matchUserId);
			if (arenaData == null) {
				continue;
			}

			double pro = 0;
			int rankIndex = getArenaRankIndex(arenaData.getUserId());
			if (rankIndex != -1) {
				pro += AngelArrayConst.ARENA_RANK_INDEX_RATE / rankIndex;
			}

			// TODO HC 竞技场上次打的时间
			long lastArenaTime = now;
			int dayDistance = DateUtils.getDayDistance(lastArenaTime, now);
			pro += AngelArrayConst.ARENA_FIGHT_TIME_RATE / (dayDistance + 1);
			int finalPro = (int) pro;
			proMap.put(matchUserId, finalPro);
		}

		Weight<String> weight = new Weight<String>(proMap);
		String ranResult = weight.getRanResult();
		if (ranResult == null) {
			GameLog.error("万仙阵匹配", userId, String.format("等级下限[%s]，上[%s]，战力下[%s]，上[%s]，没能根据活跃度随机到人", level, maxLevel, minFighting, maxFighting));
			return null;
		}

		enemyIdList.add(ranResult);// 增加一个新的成员Id
		return getAngleArrayMatchData(ArenaBM.getInstance().getArenaData(ranResult));
	}

	/**
	 * 生成一个机器人
	 * 
	 * @param robotId
	 * @return
	 */
	public static ArmyInfo getRobotArmyInfo(int robotId) {
		return null;
	}

	/**
	 * 临时先从竞技场获取到数据
	 * 
	 * @param arenaData
	 * @return
	 */
	private static ArmyInfo getAngleArrayMatchData(TableArenaData arenaData) {
		if (arenaData == null) {
			GameLog.error("万仙阵匹配", "未知角色Id", "获取不到对应的TableArenaData竞技数据");
			return null;
		}

		String userId = arenaData.getUserId();
		List<String> arenaHeroList = arenaData.getHeroIdList();
		List<String> heroIdList = new ArrayList<String>();
		for (String id : arenaHeroList) {
			if (!id.equals(userId)) {
				heroIdList.add(id);
			}
		}

		ArmyInfo armyInfo = ArmyInfoHelper.getArmyInfo(arenaData.getUserId(), heroIdList);// 战斗中的部队信息
		if (armyInfo == null) {
			GameLog.error("万仙阵匹配", userId, String.format("从竞技场拿不到角色[%s]对应的ArmyInfo", arenaData.getUserId()));
			return null;
		}

		// 角色的初始血量能量
		CurAttrData playerAttrData;
		playerAttrData = armyInfo.getPlayer().getCurAttrData();
		if (playerAttrData == null) {
			playerAttrData = new CurAttrData();
			armyInfo.getPlayer().setCurAttrData(playerAttrData);
		}

		int currLife = armyInfo.getPlayer().getAttrData().getLife();
		playerAttrData.setCurLife(currLife);
		playerAttrData.setCurEnergy(0);
		playerAttrData.setId(armyInfo.getPlayer().getRoleBaseInfo().getId());

		// 佣兵的生命值
		for (ArmyHero armyHeroTmp : armyInfo.getHeroList()) {
			CurAttrData heroCurrAttri = armyHeroTmp.getCurAttrData();
			if (heroCurrAttri == null) {
				heroCurrAttri = new CurAttrData();
				armyHeroTmp.setCurAttrData(heroCurrAttri);
			}

			heroCurrAttri.setCurLife(armyHeroTmp.getAttrData().getLife());
			heroCurrAttri.setCurEnergy(0);
			heroCurrAttri.setId(armyHeroTmp.getRoleBaseInfo().getId());
		}

		return armyInfo;
	}

	/**
	 * 获取匹配到的角色Id列表
	 *
	 * @param userId 匹配人的角色Id
	 * @param level 匹配的最低等级
	 * @param maxLevel 匹配的最高等级
	 * @param minFighting 匹配的低战力
	 * @param maxFighting 匹配的高战力
	 * @param enemyIdList 已经匹配到的数据
	 * @return
	 */
	private static List<String> getMatchUsetIdList(String userId, int level, int maxLevel, int minFighting, int maxFighting, List<String> enemyIdList) {
		List<String> userIdList = new ArrayList<String>();
		Ranking<AngleArrayComparable, AngleArrayAttribute> ranking = RankingFactory.getRanking(RankType.ANGLE_ARRAY_RANK);
		if (ranking == null) {
			return userIdList;
		}

		AngleArrayComparable minValue = new AngleArrayComparable();
		minValue.setLevel(level);
		minValue.setFighting(minFighting);

		AngleArrayComparable maxValue = new AngleArrayComparable();
		maxValue.setLevel(maxLevel);
		maxValue.setFighting(maxFighting);

		SegmentList<? extends MomentRankingEntry<AngleArrayComparable, AngleArrayAttribute>> segmentList = ranking.getSegmentList(minValue, maxValue);
		int refSize = segmentList.getRefSize();
		for (int i = 0; i < refSize; i++) {
			MomentRankingEntry<AngleArrayComparable, AngleArrayAttribute> momentRankingEntry = segmentList.get(i);
			if (momentRankingEntry == null) {
				continue;
			}

			AngleArrayAttribute extendedAttribute = momentRankingEntry.getExtendedAttribute();
			String id = extendedAttribute.getUserId();
			if (enemyIdList.contains(id) || userIdList.contains(id)) {
				continue;
			}

			userIdList.add(id);

			if (userIdList.size() >= AngelArrayConst.MAX_MATCH_SIZE) {
				break;
			}
		}

		// System.err.println("等级&战力匹配---" + userIdList.toString());
		return userIdList;
	}

	/**
	 * 通过战力榜匹配数据
	 * 
	 * @param userId 匹配人的角色Id
	 * @param rankCount 需要从战力榜中补多少个出来
	 * @param hasMatchList 已经匹配到的数据
	 * @return
	 */
	private static void getFightingRankMatch(String userId, int rankCount, int minFighting, int maxFighting, List<String> hasMatchList, List<String> enemyIdList) {
		Ranking<FightingComparable, RankingLevelData> rank = RankingFactory.getRanking(RankType.FIGHTING_ALL_DAILY);
		if (rank == null) {
			return;
		}

		// 先以最低最高战力随机几个
		FightingComparable minComparable = new FightingComparable();
		minComparable.setFighting(minFighting);

		FightingComparable maxComparable = new FightingComparable();
		maxComparable.setFighting(maxFighting);

		SegmentList<? extends MomentRankingEntry<FightingComparable, RankingLevelData>> segmentList = rank.getSegmentList(minComparable, maxComparable);
		int refSize = segmentList.getRefSize();

		int matchCount = 0;
		for (int i = 0; i < refSize; i++) {
			MomentRankingEntry<FightingComparable, RankingLevelData> momentRankingEntry = segmentList.get(i);
			if (momentRankingEntry == null) {
				continue;
			}

			RankingLevelData rankingLevelData = momentRankingEntry.getExtendedAttribute();
			if (rankingLevelData == null) {
				continue;
			}

			String id = rankingLevelData.getUserId();
			if (enemyIdList.contains(id) || hasMatchList.contains(id)) {
				continue;
			}

			hasMatchList.add(id);
			matchCount++;

			if (matchCount >= rankCount) {
				break;
			}
		}

		int offCount = rankCount - matchCount;
		if (offCount <= 0) {
			return;
		}

		// 先以战力向上浮动
		// 获取大于当前最大战力的最小排行名次
		int lowerRanking = rank.higherRanking(maxComparable);
		if (lowerRanking != -1) {
			for (int i = lowerRanking; i >= 1; --i) {
				RankingEntry<FightingComparable, RankingLevelData> rankingEntry = rank.getRankingEntry(i);
				if (rankingEntry == null) {
					continue;
				}

				RankingLevelData rankingLevelData = rankingEntry.getExtendedAttribute();
				if (rankingLevelData == null) {
					continue;
				}

				String id = rankingLevelData.getUserId();
				if (enemyIdList.contains(id) || hasMatchList.contains(id)) {
					continue;
				}

				hasMatchList.add(id);
				offCount--;

				if (offCount <= 0) {
					break;
				}
			}
		}

		// 已经够了
		if (offCount <= 0) {
			return;
		}

		int maxRanking = rank.size();
		int higherRanking = rank.lowerRanking(minComparable);
		if (higherRanking != -1) {
			for (int i = higherRanking; i <= maxRanking; i++) {
				RankingEntry<FightingComparable, RankingLevelData> rankingEntry = rank.getRankingEntry(i);
				if (rankingEntry == null) {
					continue;
				}

				RankingLevelData rankingLevelData = rankingEntry.getExtendedAttribute();
				if (rankingLevelData == null) {
					continue;
				}

				String id = rankingLevelData.getUserId();
				if (enemyIdList.contains(id) || hasMatchList.contains(id)) {
					continue;
				}

				hasMatchList.add(id);
				offCount--;

				if (offCount <= 0) {
					break;
				}
			}
		}
	}

	/**
	 * 获取角色在竞技场中的排名
	 * 
	 * @param userId
	 * @return
	 */
	private static int getArenaRankIndex(String userId) {
		PlayerIF player = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
		if (player == null) {
			return -1;
		}

		int career = player.getMainRoleHero().getCareer();
		ECareer careerType = ECareer.valueOf(career);
		int rankType;
		switch (careerType) {
		case Magican:
			rankType = RankType.MAGICAN_ARENA_DAILY.getType();
			break;
		case Priest:
			rankType = RankType.PRIEST_ARENA_DAILY.getType();
			break;
		case SwordsMan:
			rankType = RankType.SWORDMAN_ARENA_DAILY.getType();
			break;
		case Warrior:
			rankType = RankType.WARRIOR_ARENA_DAILY.getType();
			break;
		default:
			rankType = -1;
			break;
		}

		if (rankType == -1) {
			return -1;
		}

		Ranking ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			return -1;
		}

		return ranking.getRanking(userId);
	}
}