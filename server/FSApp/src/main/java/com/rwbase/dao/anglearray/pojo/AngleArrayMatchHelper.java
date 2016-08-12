package com.rwbase.dao.anglearray.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.arena.ArenaBM;
import com.bm.rank.RankType;
import com.bm.rank.anglearray.AngleArrayComparable;
import com.bm.rank.fightingAll.FightingComparable;
import com.bm.rank.teaminfo.AngelArrayTeamInfoAttribute;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.bm.robot.RobotHeroBuilder;
import com.bm.robot.RobotHeroBuilder.BuildRoleInfo;
import com.common.Weight;
import com.log.GameLog;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.HeroMgrIF;
import com.playerdata.readonly.PlayerIF;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.TeamInfo;
import com.rw.fsutil.common.SegmentList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.anglearray.AngelArrayConst;
import com.rwbase.dao.anglearray.pojo.db.AngelArrayTeamInfoData;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

/*
 * @author HC
 * @date 2015年11月13日 下午3:47:37
 * @Description 万仙阵匹配的处理
 */
public final class AngleArrayMatchHelper {

	private static final Comparator<String> COMPARATOR = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			int rankIndex1 = getArenaRankIndex(o1);
			int rankIndex2 = getArenaRankIndex(o2);
			if (rankIndex1 == rankIndex2) {
				return 0;
			}

			if (rankIndex1 <= -1) {
				return 1;
			}

			if (rankIndex2 <= -1) {
				return -1;
			}

			return rankIndex1 - rankIndex2;
		}
	};

	/**
	 * 获取匹配到的阵容信息
	 * 
	 * @param userId 角色Id
	 * @param level 角色等级
	 * @param maxLevel 最大等级
	 * @param minFighting 最小战力
	 * @param maxFighting 最大战力
	 * @param enemyIdList 已经匹配到的人的Id列表
	 * @param hasUserIdList
	 * @param robotId
	 * @return
	 */
	public static AngelArrayTeamInfoData getMatchAngelArrayTeamInfo(String userId, int level, int maxLevel, int minFighting, int maxFighting, List<String> enemyIdList, List<String> hasUserIdList,
		int robotId) {
		// 第一步等级匹配 && 第二步战力匹配
		List<String> matchUsetIdList = getMatchUsetIdList(userId, level, maxLevel, minFighting, maxFighting, enemyIdList, hasUserIdList, RankType.ANGEL_TEAM_INFO_RANK);

		// 检查是否达到了最低保底数量，没有就通过战力榜去匹配到20个
		int matchSize = matchUsetIdList.size();
		int needFightingRankCount = AngelArrayConst.MIN_MATCH_SIZE - matchSize;
		if (needFightingRankCount > 0) {
			getFightingRankMatch(userId, needFightingRankCount, minFighting, maxFighting, matchUsetIdList, enemyIdList, hasUserIdList);
		}

		// 检查下限
		final int lowFighting = (int) (minFighting * (1 - AngelArrayConst.USE_ROBOT_LOW_RATE));
		// 检查上限是否符合要求
		final int highFighting = (int) (maxFighting * (1 + AngelArrayConst.USE_ROBOT_HIGH_RATE));

		// 如果没有匹配到任何人
		TeamInfo finalTeamInfo = null;
		RankingEntry<AngleArrayComparable, AngelArrayTeamInfoAttribute> matchRankingEntry = null;
		String ranResult = null;

		// 排行榜
		Ranking<AngleArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		if (!matchUsetIdList.isEmpty()) {
			// 对随机到的人进行一次优先级排序
			Collections.sort(matchUsetIdList, COMPARATOR);
			// 第三步找出来的人进行排名权重
			// 第四步竞技活跃权重

			long now = System.currentTimeMillis();
			// 登陆时间
			Map<String, Integer> proMap = new HashMap<String, Integer>();
			for (int i = 0, size = matchUsetIdList.size(); i < size; i++) {
				String matchUserId = matchUsetIdList.get(i);

				double pro = AngelArrayConst.ARENA_RANK_INDEX_RATE / (i + 1);

				// TODO HC 竞技场上次打的时间
				RankingEntry<AngleArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(matchUserId);
				int dayDistance = AngelArrayConst.DEFAULT_LOGOUT_DAYS;
				if (rankingEntry != null) {
					AngelArrayTeamInfoAttribute extendedAttribute = rankingEntry.getExtendedAttribute();
					if (extendedAttribute != null) {
						long lastArenaTime = extendedAttribute.getTime();
						if (lastArenaTime > 0) {
							dayDistance = DateUtils.getDayDistance(lastArenaTime, now);
						}
					}
				}
				pro += AngelArrayConst.ARENA_FIGHT_TIME_RATE / (dayDistance + 1);
				int finalPro = (int) pro;
				proMap.put(matchUserId, finalPro);
			}

			Weight<String> weight = new Weight<String>(proMap);
			ranResult = weight.getRanResult();
			if (ranResult == null) {
				GameLog.error("万仙阵匹配", userId, String.format("等级下限[%s]，上[%s]，战力下[%s]，上[%s]，没能根据活跃度随机到人", level, maxLevel, minFighting, maxFighting));
				return null;
			}

			enemyIdList.add(ranResult);// 增加一个新的成员Id

			// 查找到阵容信息
			matchRankingEntry = ranking.getRankingEntry(ranResult);
			TeamInfo teamInfo = null;
			if (matchRankingEntry != null) {// 如果没有就找竞技场
				teamInfo = matchRankingEntry.getExtendedAttribute().getTeamInfo();
			}

			boolean hasTeam = false;
			String groupName = "";
			String headId = "";
			String playerName = "";
			int career = 0;

			int teamFighting = 0;
			List<Integer> heroModelIdList = new ArrayList<Integer>();
			if (teamInfo == null) {// 阵容信息为空
				TableArenaData arenaData = ArenaBM.getInstance().getArenaData(ranResult);
				PlayerIF readOnlyPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(ranResult);
				if (arenaData != null) {
					// 先找攻击阵容<模版Id>
					List<String> atkHeroList = arenaData.getAtkList();// 阵容信息
					if (atkHeroList != null && !atkHeroList.isEmpty() && readOnlyPlayer != null) {
						if (atkHeroList.contains(ranResult)) {
							atkHeroList.remove(ranResult);
						}
						teamFighting = AngelArrayTeamInfoHelper.getTeamInfoHeroModelListById(readOnlyPlayer, atkHeroList, heroModelIdList);
						hasTeam = true;
					} else {
						// 再找防守阵容
						List<String> heroIdList = arenaData.getHeroIdList();// 防守阵容信息
						if (heroIdList != null && !heroIdList.isEmpty() && readOnlyPlayer != null) {
							if (atkHeroList.contains(ranResult)) {
								atkHeroList.remove(ranResult);
							}
							teamFighting = AngelArrayTeamInfoHelper.getTeamInfoHeroModelListByUUIDList(readOnlyPlayer, heroIdList, heroModelIdList);
							hasTeam = true;
						}
					}
				}

				if (readOnlyPlayer != null) {// 从角色身上取阵容
					if (!hasTeam) {
						HeroMgrIF heroMgr = readOnlyPlayer.getHeroMgr();

						int mainRoleModelId = readOnlyPlayer.getModelId();

						heroModelIdList.add(mainRoleModelId);
						teamFighting += readOnlyPlayer.getMainRoleHero().getFighting();

						List<? extends HeroIF> maxFightingHeros = heroMgr.getMaxFightingHeros();
						for (int i = 0, size = maxFightingHeros.size(); i < size; i++) {
							HeroIF hero = maxFightingHeros.get(i);
							if (hero == null) {
								continue;
							}

							int modelId = hero.getModelId();
							if (mainRoleModelId == modelId) {
								continue;
							}

							teamFighting += hero.getFighting();
							if (!heroModelIdList.contains(modelId)) {
								heroModelIdList.add(modelId);
							}
						}
					}

					groupName = readOnlyPlayer.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupName();
					headId = readOnlyPlayer.getHeadImage();
					playerName = readOnlyPlayer.getUserName();
					career = readOnlyPlayer.getMainRoleHero().getCareer();

					hasTeam = true;
				}
			} else {
				teamFighting = teamInfo.getTeamFighting();
				heroModelIdList = getHeroModelIdList(teamInfo.getHero());

				hasTeam = true;
				groupName = teamInfo.getGroupName();
				headId = teamInfo.getHeadId();
				playerName = teamInfo.getName();
				career = teamInfo.getCareer();
			}

			boolean fit = false;
			if (teamFighting >= lowFighting && teamFighting <= highFighting) {
				fit = true;
			}

			if (fit) {// 正好满足条件
				if (!hasTeam) {
					finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId);
				} else {
					finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId, new BuildRoleInfo(ranResult, playerName, headId, groupName, career, heroModelIdList), true);
				}
			} else {
				if (!hasTeam) {
					finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId);
				} else {
					finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId, new BuildRoleInfo(ranResult, playerName, headId, groupName, career, heroModelIdList), true);
				}
			}
		} else {
			finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId);
			ranResult = finalTeamInfo.getUuid();
		}

		int matchFighting = finalTeamInfo.getTeamFighting();
		if (matchRankingEntry != null) {
			matchRankingEntry.getExtendedAttribute().setTeamInfo(finalTeamInfo);
			ranking.subimitUpdatedTask(matchRankingEntry);
		} else {// 如果没有就添加到排行榜
			AngleArrayComparable comparable = new AngleArrayComparable();
			comparable.setFighting(matchFighting);
			comparable.setLevel(finalTeamInfo.getLevel());

			AngelArrayTeamInfoAttribute attribute = new AngelArrayTeamInfoAttribute();
			attribute.setTime(System.currentTimeMillis());
			attribute.setUserId(ranResult);
			attribute.setTeamInfo(finalTeamInfo);

			ranking.addOrUpdateRankingEntry(ranResult, comparable, attribute);
		}

		GameLog.info("万仙阵匹配的数据", userId, String.format("匹配最低战力【%s】，最高战力【%s】，等级【%s】，浮动下限【%s】，浮动上限【%s】，匹配之后的战力【%s】，名字【%s】，ID【%s】", minFighting, maxFighting, level, lowFighting, highFighting,
			(finalTeamInfo != null ? matchFighting : 0), (finalTeamInfo != null ? finalTeamInfo.getName() : ""), ranResult));

		AngelArrayTeamInfoData angelArrayTeamInfoData = new AngelArrayTeamInfoData();
		int saveMinFighting = (int) (matchFighting * (1 - AngelArrayConst.SAVE_TEAM_INFO_FIGHTING_LOW_RATE));
		int saveMaxFighting = (int) (matchFighting * (1 + AngelArrayConst.SAVE_TEAM_INFO_FIGHTING_HIGH_RATE));

		angelArrayTeamInfoData.setMinFighting(saveMinFighting);
		angelArrayTeamInfoData.setMaxFighting(saveMaxFighting);
		angelArrayTeamInfoData.setTeamInfo(finalTeamInfo);
		angelArrayTeamInfoData.setUserId(ranResult);

		// 增加已经产生的Id，防止重复
		hasUserIdList.add(ranResult);
		return angelArrayTeamInfoData;
	}

	/**
	 * 获取所有的英雄Id列表
	 * 
	 * @param hero
	 * @return
	 */
	private static List<Integer> getHeroModelIdList(List<HeroInfo> hero) {
		RoleCfgDAO roleCfgDAO = RoleCfgDAO.getInstance();
		List<Integer> heroModelIdList = new ArrayList<Integer>();
		if (hero == null || hero.isEmpty()) {
			return heroModelIdList;
		}

		for (int i = 0, size = hero.size(); i < size; i++) {
			HeroInfo heroInfo = hero.get(i);
			if (heroInfo == null) {
				continue;
			}

			String tmpId = heroInfo.getBaseInfo().getTmpId();
			RoleCfg roleCfg = roleCfgDAO.getCfgById(tmpId);
			if (roleCfg == null) {
				continue;
			}

			heroModelIdList.add(roleCfg.getModelId());
		}

		return heroModelIdList;
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
	private static List<String> getMatchUsetIdList(String userId, int level, int maxLevel, int minFighting, int maxFighting, List<String> enemyIdList, List<String> hasUserIdList, RankType rankType) {
		List<String> userIdList = new ArrayList<String>();
		Ranking<AngleArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			GameLog.error("从万仙阵阵容榜中匹配", userId, String.format("排行榜类型[%s]万仙阵阵容的排行榜是Null", rankType));
			return userIdList;
		}

		AngleArrayComparable minValue = new AngleArrayComparable();
		minValue.setLevel(level);
		minValue.setFighting(minFighting);

		AngleArrayComparable maxValue = new AngleArrayComparable();
		maxValue.setLevel(maxLevel);
		maxValue.setFighting(maxFighting);

		SegmentList<? extends MomentRankingEntry<AngleArrayComparable, AngelArrayTeamInfoAttribute>> segmentList = ranking.getSegmentList(minValue, maxValue);
		int refSize = segmentList.getRefSize();
		if (refSize <= 0) {
			GameLog.error("从万仙阵阵容榜中匹配", userId, String.format("需要匹配的战力上下限是[%s,%s]不能从类型为[%s]榜中截取到任何数据", minFighting, maxFighting, rankType));
			return userIdList;
		}

		for (int i = 0; i < refSize; i++) {
			MomentRankingEntry<AngleArrayComparable, AngelArrayTeamInfoAttribute> momentRankingEntry = segmentList.get(i);
			if (momentRankingEntry == null) {
				continue;
			}

			AngelArrayTeamInfoAttribute extendedAttribute = momentRankingEntry.getExtendedAttribute();
			String id = extendedAttribute.getUserId();
			if (enemyIdList.contains(id) || userIdList.contains(id) || hasUserIdList.contains(id)) {
				continue;
			}

			userIdList.add(id);

			if (userIdList.size() >= AngelArrayConst.MAX_MATCH_SIZE) {
				break;
			}
		}

		if (userIdList.isEmpty()) {
			GameLog.error("从万仙阵阵容榜中匹配", userId, String.format("需要匹配的战力上下限是[%s,%s]不能从类型为[%s]榜中匹配到任何玩家数据", minFighting, maxFighting, rankType));
		}
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
	private static void getFightingRankMatch(String userId, int rankCount, int minFighting, int maxFighting, List<String> hasMatchList, List<String> enemyIdList, List<String> hasUserIdList) {
		Ranking<FightingComparable, RankingLevelData> rank = RankingFactory.getRanking(RankType.FIGHTING_ALL_DAILY);
		if (rank == null) {
			GameLog.error("通过战力榜匹配数据", RankType.FIGHTING_ALL_DAILY.getName(), "获取不到对应某个排行榜的数据");
			return;
		}

		// 先以最低最高战力随机几个
		FightingComparable minComparable = new FightingComparable();
		minComparable.setFighting(minFighting);

		FightingComparable maxComparable = new FightingComparable();
		maxComparable.setFighting(maxFighting);

		SegmentList<? extends MomentRankingEntry<FightingComparable, RankingLevelData>> segmentList = rank.getSegmentList(minComparable, maxComparable);
		int refSize = segmentList.getRefSize();

		boolean nonPerson = true;// 是否有匹配到人

		if (refSize <= 0) {
			GameLog.info("通过战力榜匹配数据", RankType.FIGHTING_ALL_DAILY.getName(), String.format("匹配区间[%s,%s]中没有在排行榜中截取到数据", minFighting, maxFighting));
		}

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
			if (enemyIdList.contains(id) || hasMatchList.contains(id) || hasUserIdList.contains(id)) {
				continue;
			}

			hasMatchList.add(id);
			matchCount++;
			nonPerson = false;

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
				if (enemyIdList.contains(id) || hasMatchList.contains(id) || hasUserIdList.contains(id)) {
					continue;
				}

				hasMatchList.add(id);
				offCount--;
				nonPerson = false;

				if (offCount <= 0) {
					break;
				}
			}
		} else {
			GameLog.info("通过战力榜匹配数据", RankType.FIGHTING_ALL_DAILY.getName(), String.format("战力[%s]上浮不到任何人", maxFighting));
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
				if (enemyIdList.contains(id) || hasMatchList.contains(id) || hasUserIdList.contains(id)) {
					continue;
				}

				hasMatchList.add(id);
				offCount--;
				nonPerson = false;

				if (offCount <= 0) {
					break;
				}
			}
		} else {
			GameLog.info("通过战力榜匹配数据", RankType.FIGHTING_ALL_DAILY.getName(), String.format("战力[%s]下浮不到任何人", minFighting));
		}

		// 匹配到最后都没从战力榜获取到人
		if (nonPerson) {
			GameLog.info("通过战力榜匹配数据", RankType.FIGHTING_ALL_DAILY.getName(), "从战力榜中没有上下浮动到任何人");
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