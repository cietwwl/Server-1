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
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.ranking.pojo.RankingLevelData;

/*
 * @author HC
 * @date 2015年11月13日 下午3:47:37
 * @Description 万仙阵匹配的处理
 */
public final class AngleArrayMatchHelper {

	private static final int MIN_MATCH_SIZE = 20;// 保底20个人
	private static final int MAX_MATCH_SIZE = 50;// 上限50个人
	private static final int ARENA_RANK_INDEX_RATE = 50;// 排名权重
	private static final int ARENA_FIGHT_TIME_RATE = 50;// 打竞技场的活跃度权重

	// private static final int MIN_LEVEL = 20;// 万仙阵最低等级
	//
	// private static final Comparator<Integer> comparator = new Comparator<Integer>() {
	//
	// @Override
	// public int compare(Integer o1, Integer o2) {
	// return o2.intValue() - o1.intValue();
	// }
	// };
	//
	// private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	// private static WriteLock wLock = lock.writeLock();
	// private static ReadLock rLock = lock.readLock();

	// /**
	// * 全局的随机
	// */
	// private static Random ran = new Random();
	// /**
	// * 匹配对手的总列表,对应的Key<战斗力,List<角色Id>>
	// */
	// private static volatile TreeMap<Integer, List<String>> matchMap;

	// /** 匹配对手的列表，对应的Key<等级，<战力，List<角色Id>>> */
	// private static TreeMap<Integer, TreeMap<Integer, List<String>>> matchMap;// 新版数据

	public static void resetMatchData() {
		// wLock.lock();
		// try {
		// matchMap = new TreeMap<Integer, TreeMap<Integer, List<String>>>();// 新版匹配
		// // 初始化一下匹配列表的等级Key
		// List<Integer> keys = AngleArrayMatchCfgCsvDao.getCfgDAO().getAngleArrayMatchKeys();
		// for (int i = 0, size = keys.size(); i < size; i++) {
		// Integer integer = keys.get(i);
		// matchMap.put(integer, new TreeMap<Integer, List<String>>());
		// }
		//
		// Ranking rank = RankingFactory.getRanking(RankType.FIGHTING_ALL_DAILY);// 昨日战力榜
		//
		// // long s1 = System.currentTimeMillis();
		// List<ListRankingEntry<String, ArenaExtAttribute>> arenaInfoList = ArenaBM.getInstance().getArenaInfoList(ListRankingType.WARRIOR_ARENA);
		// // long e1 = System.currentTimeMillis();
		// // System.err.println("步骤1：" + (e1 - s1) + "毫秒");
		// // long s2 = System.currentTimeMillis();
		// setData(arenaInfoList, rank);
		// // long e2 = System.currentTimeMillis();
		// // System.err.println("步骤2：" + (e2 - s2) + "毫秒");
		// // long s3 = System.currentTimeMillis();
		// List<ListRankingEntry<String, ArenaExtAttribute>> arenaInfoList2 = ArenaBM.getInstance().getArenaInfoList(ListRankingType.PRIEST_ARENA);
		// // long e3 = System.currentTimeMillis();
		// // System.err.println("步骤3：" + (e3 - s3) + "毫秒");
		// // long s4 = System.currentTimeMillis();
		// setData(arenaInfoList2, rank);
		// // long e4 = System.currentTimeMillis();
		// // System.err.println("步骤4：" + (e4 - s4) + "毫秒");
		// // long s5 = System.currentTimeMillis();
		// List<ListRankingEntry<String, ArenaExtAttribute>> arenaInfoList3 = ArenaBM.getInstance().getArenaInfoList(ListRankingType.SWORDMAN_ARENA);
		// // long e5 = System.currentTimeMillis();
		// // System.err.println("步骤5：" + (e5 - s5) + "毫秒");
		// // long s6 = System.currentTimeMillis();
		// setData(arenaInfoList3, rank);
		// // long e6 = System.currentTimeMillis();
		// // System.err.println("步骤6：" + (e6 - s6) + "毫秒");
		// // long s7 = System.currentTimeMillis();
		// List<ListRankingEntry<String, ArenaExtAttribute>> arenaInfoList4 = ArenaBM.getInstance().getArenaInfoList(ListRankingType.MAGICAN_ARENA);
		// // long e7 = System.currentTimeMillis();
		// // System.err.println("步骤7：" + (e7 - s7) + "毫秒");
		// // long s8 = System.currentTimeMillis();
		// setData(arenaInfoList4, rank);
		// // long e8 = System.currentTimeMillis();
		// // System.err.println("步骤8：" + (e8 - s8) + "毫秒");
		// //
		// // for (Entry<Integer, TreeMap<Integer, List<String>>> e : matchMap.entrySet()) {
		// // StringBuilder sb = new StringBuilder();
		// // sb.append("level:").append(e.getKey()).append(";fighting");
		// // TreeMap<Integer, List<String>> value = e.getValue();
		// // for (Entry<Integer, List<String>> en : value.entrySet()) {
		// // sb.append("[").append(en.getKey()).append("]").append(en.getValue().toString()).append(";");
		// // }
		// // System.err.println(sb.toString());
		// // }
		// } finally {
		// wLock.unlock();
		// }
	}

	// /**
	// * 更新万仙阵匹配数据
	// *
	// * @param dataList
	// * @param rank
	// */
	// private static void setData(List<ListRankingEntry<String, ArenaExtAttribute>> dataList, Ranking rank) {
	// if (dataList == null || dataList.isEmpty()) {
	// return;
	// }
	//
	// // long loopTime = 0;
	// // long totalTime = 0;
	// // long rankTime = 0;
	// // long fightTime = 0;
	// for (int i = 0, size = dataList.size(); i < size; i++) {
	// ListRankingEntry<String, ArenaExtAttribute> entry = dataList.get(i);
	// String userId = entry.getKey();
	//
	// // long s = System.currentTimeMillis();
	// TableArenaData arenaData = ArenaBM.getInstance().getArenaData(userId);
	// // long e = System.currentTimeMillis();
	// // totalTime += (e - s);
	// if (arenaData == null) {
	// continue;
	// }
	//
	// // long s2 = System.currentTimeMillis();
	// // 从排行榜中获取无人小队战力
	// int totalFighting = -1;
	// if (rank != null) {
	// RankingEntry rankingEntry = rank.getRankingEntry(userId);
	// if (rankingEntry != null) {
	// RankingLevelData att = (RankingLevelData) rankingEntry.getExtendedAttribute();
	// if (att != null) {
	// totalFighting = att.getFightingTeam();
	// }
	// }
	// }
	// // long e2 = System.currentTimeMillis();
	// // rankTime += (e2 - s2);
	// //
	// // long s3 = System.currentTimeMillis();
	// if (totalFighting == -1) {
	// PlayerIF p = PlayerMgr.getInstance().getReadOnlyPlayer(userId);
	// totalFighting = p.getMainRoleHero().getFighting() + p.getHeroMgr().getFightingTeam();
	// }
	// // long e3 = System.currentTimeMillis();
	// // fightTime += (e3 - s3);
	//
	// int level = arenaData.getLevel();
	// if (level < MIN_LEVEL) {
	// continue;
	// }
	//
	// // long s1 = System.currentTimeMillis();
	// TreeMap<Integer, List<String>> fightingMap;
	// Integer floorKey = matchMap.floorKey(level);
	// if (floorKey == null) {
	// fightingMap = new TreeMap<Integer, List<String>>();
	// matchMap.put(level, fightingMap);
	// } else {
	// fightingMap = matchMap.get(floorKey);
	// }
	//
	// List<String> userIdList = fightingMap.get(totalFighting);
	// if (userIdList == null) {
	// userIdList = new ArrayList<String>();
	// fightingMap.put(totalFighting, userIdList);
	// }
	//
	// userIdList.add(userId);
	// // long e1 = System.currentTimeMillis();
	// // loopTime += (e1 - s1);
	// }
	//
	// // System.err.println("消耗总时间：" + totalTime);
	// // System.err.println("循环总消耗时间：" + loopTime);
	// // System.err.println("战力消耗时间：" + fightTime);
	// // System.err.println("排行获取数据时间：" + rankTime);
	// }

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
		int needFightingRankCount = MIN_MATCH_SIZE - matchSize;
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
				pro += ARENA_RANK_INDEX_RATE / rankIndex;
			}

			// TODO HC 竞技场上次打的时间
			long lastArenaTime = now;
			int dayDistance = DateUtils.getDayDistance(lastArenaTime, now);
			pro += ARENA_FIGHT_TIME_RATE / (dayDistance + 1);
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

			if (userIdList.size() >= MAX_MATCH_SIZE) {
				break;
			}
		}

		// System.err.println("等级&战力匹配---" + userIdList.toString());
		return userIdList;
	}

	// /**
	// * 获取匹配到的角色Id列表
	// *
	// * @param userId 匹配人的角色Id
	// * @param level 匹配人的等级
	// * @param minFighting 匹配的低战力
	// * @param maxFighting 匹配的高战力
	// * @return
	// */
	// private static List<String> getMatchUsetIdList(String userId, int level, int minFighting, int maxFighting) {
	// rLock.lock();
	// try {
	// List<String> userIdList = new ArrayList<String>();
	//
	// Entry<Integer, TreeMap<Integer, List<String>>> floorEntry = matchMap.floorEntry(level);
	// if (floorEntry == null) {
	// return userIdList;
	// }
	//
	// TreeMap<Integer, List<String>> value = floorEntry.getValue();
	// if (value == null) {
	// return userIdList;
	// }
	//
	// SortedMap<Integer, List<String>> subMap = value.subMap(minFighting, maxFighting);
	// if (subMap == null || subMap.isEmpty()) {
	// return userIdList;
	// }
	//
	// if (subMap.size() > 1) {
	// List<Integer> keyList = new ArrayList<Integer>();
	// Iterator<Integer> itr = subMap.keySet().iterator();
	// while (itr.hasNext()) {
	// keyList.add(itr.next());
	// }
	//
	// Collections.sort(keyList, comparator);
	//
	// for (int i = 0, size = keyList.size(); i < size; i++) {
	// List<String> list = subMap.get(keyList.get(i));
	// if (list == null || list.isEmpty()) {
	// continue;
	// }
	//
	// if (addMatchUserId(userId, userIdList, list)) {
	// break;
	// }
	// }
	// } else {
	// for (Entry<Integer, List<String>> e : subMap.entrySet()) {
	// List<String> list = e.getValue();
	// if (list == null || list.isEmpty()) {
	// continue;
	// }
	//
	// if (addMatchUserId(userId, userIdList, list)) {
	// break;
	// }
	// }
	// }
	//
	// return userIdList;
	// } finally {
	// rLock.unlock();
	// }
	// }
	//
	// /**
	// * 增加匹配到的角色Id数据
	// *
	// * @param userId
	// * @param userIdList
	// * @param list
	// * @return
	// */
	// private static boolean addMatchUserId(String userId, List<String> userIdList, List<String> list) {
	// for (int i = 0, size = list.size(); i < size; i++) {
	// if (userIdList.size() >= MAX_MATCH_SIZE) {
	// return true;
	// }
	//
	// String e = list.get(i);
	// if (!StringUtils.isEmpty(e) && !e.equals(userId) && !userIdList.contains(e)) {
	// userIdList.add(e);
	// }
	// }
	//
	// return false;
	// }

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

		// int ranking = rank.getRanking(userId);
		// if (ranking == -1 || ranking == maxRanking) {// 没有进入战力榜或者是最后一名
		// for (int i = 0; i < rankCount;) {
		// int rankIndex = maxRanking - i;
		// if (rankIndex < 1) {
		// break;
		// }
		//
		// RankingEntry rankingEntry = rank.getRankingEntry(rankIndex);
		// if (rankingEntry == null) {
		// continue;
		// }
		//
		// String key = rankingEntry.getKey();
		// if (!hasMatchList.contains(key)) {
		// hasMatchList.add(key);
		// i++;
		// }
		// }
		// } else {// 进榜进行上下浮
		// // 先下浮
		// int lowOffSize = 0;
		// for (int i = 1; i <= rankCount;) {
		// int rankIndex = ranking + i;
		// if (rankIndex > maxRanking) {// 超出排行榜的数据了
		// break;
		// }
		//
		// RankingEntry rankingEntry = rank.getRankingEntry(rankIndex);
		// if (rankingEntry == null) {
		// continue;
		// }
		//
		// String key = rankingEntry.getKey();
		// if (!hasMatchList.contains(key)) {
		// hasMatchList.add(key);
		// i++;
		// lowOffSize++;
		// }
		// }
		//
		// int upOffSize = rankCount - lowOffSize;
		//
		// // 如果下浮之后还不够，再上浮
		// if (upOffSize <= 0 || ranking == 1) {// 排名第一
		// return;
		// }
		//
		// for (int i = 0; i < upOffSize;) {
		// int rankIndex = ranking - i;
		// if (rankIndex < 1) {
		// break;
		// }
		//
		// RankingEntry rankingEntry = rank.getRankingEntry(rankIndex);
		// if (rankingEntry == null) {
		// continue;
		// }
		//
		// String key = rankingEntry.getKey();
		// if (!hasMatchList.contains(key)) {
		// hasMatchList.add(key);
		// i++;
		// }
		// }
		// }
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

	/**
	 * 生成一个机器人
	 * 
	 * @param robotId
	 * @return
	 */
	public static ArmyInfo getRobotArmyInfo(int robotId) {
		return null;
	}
}