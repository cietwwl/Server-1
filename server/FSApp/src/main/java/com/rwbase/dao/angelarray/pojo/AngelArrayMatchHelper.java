package com.rwbase.dao.angelarray.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.arena.ArenaBM;
import com.bm.arena.RobotCfgDAO;
import com.bm.arena.RobotEntryCfg;
import com.bm.rank.RankType;
import com.bm.rank.angelarray.AngelArrayComparable;
import com.bm.rank.fightingAll.FightingComparable;
import com.bm.rank.teaminfo.AngelArrayTeamInfoAttribute;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.bm.robot.RobotHeroBuilder;
import com.bm.robot.RobotHeroBuilder.BuildRoleInfo;
import com.common.Weight;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.PlayerMgr;
import com.playerdata.embattle.EmBattlePositionKey;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.hero.core.FSHero;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.readonly.PlayerIF;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.TeamInfo;
import com.rw.fsutil.common.SegmentList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.dao.angelarray.AngelArrayConst;
import com.rwbase.dao.angelarray.pojo.db.AngelArrayTeamInfoData;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.ranking.pojo.RankingLevelData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwproto.BattleCommon.eBattlePositionType;

/*
 * @author HC
 * @date 2015年11月13日 下午3:47:37
 * @Description 万仙阵匹配的处理
 */
public class AngelArrayMatchHelper {

	private static RankType SECOND_MATCH_RANK_TYPE = RankType.TEAM_FIGHTING;// 第二层随机的榜

	private static Comparator<MatchUserInfo> COMPARATOR = new Comparator<MatchUserInfo>() {

		@Override
		public int compare(MatchUserInfo o1, MatchUserInfo o2) {
			int rankIndex1 = AngelArrayMatchHelper.getInstance().getArenaRankIndex(o1);
			int rankIndex2 = AngelArrayMatchHelper.getInstance().getArenaRankIndex(o2);
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

	private static AngelArrayMatchHelper instance = new AngelArrayMatchHelper();

	public static AngelArrayMatchHelper getInstance() {
		return instance;
	}

	protected AngelArrayMatchHelper() {
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
	 * @param hasUserIdList
	 * @param robotId
	 * @return
	 */
	public AngelArrayTeamInfoData getMatchAngelArrayTeamInfo(String userId, int level, int maxLevel, int minFighting, int maxFighting, List<String> enemyIdList, List<String> hasUserIdList, int robotId) {
		// 第一步等级匹配 && 第二步战力匹配
		Map<String, MatchUserInfo> matchUserInfo = getMatchUserInfo(userId, level, maxLevel, minFighting, maxFighting, enemyIdList, hasUserIdList, RankType.ANGEL_TEAM_INFO_RANK);

		// 检查是否达到了最低保底数量，没有就通过战力榜去匹配到20个
		int matchSize = matchUserInfo.size();
		int needFightingRankCount = AngelArrayConst.MIN_MATCH_SIZE - matchSize;
		if (needFightingRankCount > 0) {
			getFightingRankMatch(userId, needFightingRankCount, minFighting, maxFighting, matchUserInfo, enemyIdList, hasUserIdList);
		}

		// 检查下限
		final int lowFighting = (int) (minFighting * (1 - AngelArrayConst.USE_ROBOT_LOW_RATE));
		// 检查上限是否符合要求
		final int highFighting = (int) (maxFighting * (1 + AngelArrayConst.USE_ROBOT_HIGH_RATE));

		// 如果没有匹配到任何人
		TeamInfo finalTeamInfo = null;
		RankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute> matchRankingEntry = null;
		String ranResult = null;

		// StringBuilder sb = new StringBuilder();
		// sb.append(String.format("等级区间[%s,%s]，战力区间[%s,%s]，检查机器人区间[%s,%s]", level, maxLevel, minFighting, maxFighting, lowFighting, highFighting)).append("\n");

		boolean isRobot = true;// 是否是随机出来的机器人
		// 排行榜
		Ranking<AngelArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		if (!matchUserInfo.isEmpty()) {
			MatchUserInfo matchUser = getMatchUser(matchUserInfo, ranking);
			if (matchUser != null) {
				ranResult = matchUser.getUserId();
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
				PlayerIF readOnlyPlayer = null;
				if (teamInfo == null) {// 阵容信息为空
					readOnlyPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(ranResult);
					if (readOnlyPlayer != null) {
						teamFighting = getRealPlayerEmbattleInfoFighting(readOnlyPlayer, matchUser.getRankType(), heroModelIdList);
						hasTeam = !heroModelIdList.isEmpty();

						// 获取到帮派等信息
						UserGroupAttributeDataIF userGroupAttributeData = UserGroupAttributeDataMgr.getMgr().getUserGroupAttributeData(ranResult);
						groupName = userGroupAttributeData == null ? "" : userGroupAttributeData.getGroupName();
						headId = readOnlyPlayer.getHeadImage();
						playerName = readOnlyPlayer.getUserName();
						career = readOnlyPlayer.getMainRoleHero().getCareerType();
					}
				} else {
					int fighting = teamInfo.getTeamFighting();
					// int logFighting = fighting;
					boolean canUseRanking = true;
					if (matchUser.getRankType() == SECOND_MATCH_RANK_TYPE && fighting != matchUser.getFighting()) {
						readOnlyPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(ranResult);
						if (readOnlyPlayer != null) {
							teamFighting = getCopyEmbattleTeamFighting(readOnlyPlayer, heroModelIdList);
							teamInfo = AngelArrayTeamInfoHelper.parsePlayer2TeamInfo(readOnlyPlayer, heroModelIdList);
							canUseRanking = false;

							// logFighting = teamFighting;
							// sb.append("取自玩家身上真实的副本阵容：");
						}
					}

					if (canUseRanking) {
						teamFighting = fighting;
						heroModelIdList = getHeroModelIdList(teamInfo.getHero());
						// logFighting = matchRankingEntry == null ? teamFighting : matchRankingEntry.getComparable().getFighting();
						// sb.append("取自玩家万仙阵排行中的阵容：");
					}

					hasTeam = !heroModelIdList.isEmpty();

					groupName = teamInfo.getGroupName();
					headId = teamInfo.getHeadId();
					playerName = teamInfo.getName();
					career = teamInfo.getCareer();

					// sb.append(ranResult).append(",战斗力：").append(logFighting).append("\n");
				}

				boolean fit = false;
				if (teamFighting >= lowFighting && teamFighting <= highFighting) {
					fit = true;
				}

				if (fit) {// 正好满足条件
					if (teamInfo != null) {
						finalTeamInfo = teamInfo;
						isRobot = false;
						// sb.append("阵容正好合适，并且teamInfo不是空：").append(ranResult).append(",战斗力：").append(teamFighting).append("\n");
					} else {
						if (!hasTeam) {
							finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId);
							// sb.append("阵容正好合适，并且玩家没有阵容，要用纯机器人：").append(ranResult).append(",战斗力：").append(finalTeamInfo.getTeamFighting()).append(",机器人Id:").append(robotId).append("\n");
						} else {
							finalTeamInfo = AngelArrayTeamInfoHelper.parsePlayer2TeamInfo(readOnlyPlayer, heroModelIdList);
							isRobot = false;
							// sb.append("阵容正好合适，玩家有阵容，用机器人数据填充：").append(ranResult).append(",战斗力：").append(finalTeamInfo.getTeamFighting()).append(",机器人Id:").append(robotId).append("\n");
						}
					}
				} else {
					if (!hasTeam) {
						finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId);
						// sb.append("阵容不合适，并且玩家没有阵容，要用纯机器人：").append(ranResult).append(",战斗力：").append(finalTeamInfo.getTeamFighting()).append(",机器人Id:").append(robotId).append("\n");
					} else {
						finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId, new BuildRoleInfo(ranResult, playerName, headId, groupName, career, heroModelIdList), true);
						isRobot = false;
						// sb.append("阵容不合适，玩家有阵容，用机器人数据填充：").append(ranResult).append(",战斗力：").append(finalTeamInfo.getTeamFighting()).append(",机器人Id:").append(robotId).append("\n");
					}
				}
			} else {
				finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId);
				ranResult = finalTeamInfo.getUuid();
				// sb.append("没有匹配到任何数据，用纯机器人：").append(ranResult).append(",战斗力：").append(finalTeamInfo.getTeamFighting()).append(",机器人Id:").append(robotId).append("\n");
			}
		} else {
			finalTeamInfo = RobotHeroBuilder.getRobotTeamInfo(robotId);
			ranResult = finalTeamInfo.getUuid();

			// sb.append("数据完全没有。只能用纯机器人：").append(ranResult).append(",战斗力：").append(finalTeamInfo.getTeamFighting()).append(",机器人Id:").append(robotId).append("\n");
		}

		int matchFighting = finalTeamInfo.getTeamFighting();
		if (matchRankingEntry != null) {
			matchRankingEntry.getExtendedAttribute().setTeamInfo(finalTeamInfo);
			ranking.subimitUpdatedTask(matchRankingEntry);
		} else {// 如果没有就添加到排行榜
			AngelArrayComparable comparable = new AngelArrayComparable();
			comparable.setFighting(matchFighting);
			comparable.setLevel(finalTeamInfo.getLevel());

			AngelArrayTeamInfoAttribute attribute = new AngelArrayTeamInfoAttribute();
			attribute.setTime(System.currentTimeMillis());
			attribute.setUserId(ranResult);
			attribute.setTeamInfo(finalTeamInfo);

			ranking.addOrUpdateRankingEntry(ranResult, comparable, attribute);
		}

		// GameLog.info("万仙阵匹配的数据", userId,
		// String.format("匹配最低战力【%s】，最高战力【%s】，等级【%s】，浮动下限【%s】，浮动上限【%s】，匹配之后的战力【%s】，名字【%s】，ID【%s】", minFighting, maxFighting, level, lowFighting, highFighting, (finalTeamInfo != null ? matchFighting :
		// 0), (finalTeamInfo != null ? finalTeamInfo.getName() : ""), ranResult));

		// System.err.println(sb.toString());

		AngelArrayTeamInfoData angelArrayTeamInfoData = new AngelArrayTeamInfoData();
		int saveMinFighting = (int) (minFighting * (1 - AngelArrayConst.SAVE_TEAM_INFO_FIGHTING_LOW_RATE));
		int saveMaxFighting = (int) (maxFighting * (1 + AngelArrayConst.SAVE_TEAM_INFO_FIGHTING_HIGH_RATE));

		angelArrayTeamInfoData.setMinFighting(saveMinFighting);
		angelArrayTeamInfoData.setMaxFighting(saveMaxFighting);
		angelArrayTeamInfoData.setTeamInfo(finalTeamInfo);
		angelArrayTeamInfoData.setUserId(ranResult);

		if (isRobot) {
			RobotEntryCfg angelRobotCfg = RobotCfgDAO.getInstance().getRobotCfg(String.valueOf(robotId));
			if (angelRobotCfg != null) {
				angelArrayTeamInfoData.setMinFloor(angelRobotCfg.getMinLimitValue());
				angelArrayTeamInfoData.setMaxFloor(angelRobotCfg.getMaxLimitValue());
			}
		}

		// 增加已经产生的Id，防止重复
		hasUserIdList.add(ranResult);
		return angelArrayTeamInfoData;
	}

	/**
	 * 随机一个成员出来
	 * 
	 * @param matchUserInfo
	 * @param ranking
	 * @return
	 */
	private MatchUserInfo getMatchUser(Map<String, MatchUserInfo> matchUserInfo, Ranking<AngelArrayComparable, AngelArrayTeamInfoAttribute> ranking) {
		// 对随机到的人进行一次优先级排序
		ArrayList<MatchUserInfo> list = new ArrayList<MatchUserInfo>(matchUserInfo.values());
		Collections.sort(list, COMPARATOR);
		// 第三步找出来的人进行排名权重
		// 第四步竞技活跃权重

		long now = System.currentTimeMillis();
		// 登陆时间
		Map<MatchUserInfo, Integer> proMap = new HashMap<MatchUserInfo, Integer>();
		for (int i = 0, size = list.size(); i < size; i++) {
			MatchUserInfo matchUser = list.get(i);
			if (matchUser == null) {
				continue;
			}

			String matchUserId = matchUser.getUserId();

			double pro = AngelArrayConst.ARENA_RANK_INDEX_RATE / (i + 1);

			RankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(matchUserId);
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
			proMap.put(matchUser, finalPro);
		}

		Weight<MatchUserInfo> weight = new Weight<MatchUserInfo>(proMap);
		return weight.getRanResult();
	}

	/**
	 * 当数据中没有任何阵容信息的时候，从这个几个途径拿取阵容信息
	 * 
	 * @param player
	 * @param matchSource 匹配到的人的来源
	 * @param heroModelIdList
	 * @return
	 */
	private int getRealPlayerEmbattleInfoFighting(PlayerIF player, RankType matchSource, List<Integer> heroModelIdList) {
		int teamFighting = 0;
		if (matchSource == RankType.ANGEL_TEAM_INFO_RANK) {// 来源万仙阵榜
			teamFighting = getArenaTeamFighting(player, heroModelIdList);
			if (teamFighting > 0) {
				return teamFighting;
			}
		} else if (matchSource == SECOND_MATCH_RANK_TYPE) {// 来源战力榜
			teamFighting = getCopyEmbattleTeamFighting(player, heroModelIdList);
			if (teamFighting > 0) {
				return teamFighting;
			}
		}

		// 3：然后采用身上最高战力的五人
		return getStrongestHeroTeamFighting(player, heroModelIdList);
	}

	/**
	 * 获取竞技场中的阵容和战斗力
	 * 
	 * @param player
	 * @param heroModelIdList
	 * @return
	 */
	private int getArenaTeamFighting(PlayerIF player, List<Integer> heroModelIdList) {
		if (player == null) {
			return 0;
		}

		String userId = player.getUserId();
		TableArenaData arenaData = ArenaBM.getInstance().getArenaData(userId);
		if (arenaData == null) {
			return 0;
		}

		// StringBuilder sb = new StringBuilder();

		int teamFighting = 0;
		// 先找攻击阵容<模版Id>
		List<String> atkHeroList = arenaData.getAtkList();// 阵容信息
		if (atkHeroList != null && !atkHeroList.isEmpty()) {
			if (atkHeroList.contains(userId)) {
				atkHeroList.remove(userId);
			}

			teamFighting = AngelArrayTeamInfoHelper.getTeamInfoHeroModelListById(player, atkHeroList, heroModelIdList);
			// sb.append("取自竞技场排行攻击阵容：").append(userId).append(",战斗力：").append(teamFighting).append("\n");
		} else {
			// 再找防守阵容
			List<String> heroIdList = arenaData.getHeroIdList();// 防守阵容信息
			if (heroIdList != null && !heroIdList.isEmpty()) {
				if (atkHeroList.contains(userId)) {
					atkHeroList.remove(userId);
				}
				teamFighting = AngelArrayTeamInfoHelper.getTeamInfoHeroModelListByUUIDList(player, heroIdList, heroModelIdList);

				// sb.append("取自竞技场排行防守阵容：").append(userId).append(",战斗力：").append(teamFighting).append("\n");
			}
		}

		// System.err.println(sb.toString());

		return teamFighting;
	}

	/**
	 * 获取最强无人的数据
	 * 
	 * @param player
	 * @param heroModelIdList
	 * @return
	 */
	private int getStrongestHeroTeamFighting(PlayerIF player, List<Integer> heroModelIdList) {
		int teamFighting = 0;

		HeroMgr heroMgr = player.getHeroMgr();
		int mainRoleModelId = player.getModelId();

		heroModelIdList.add(mainRoleModelId);
		teamFighting += player.getMainRoleHero().getFighting();

		List<Hero> maxFightingHeros = heroMgr.getMaxFightingHeros(player);
		for (int i = 0, size = maxFightingHeros.size(); i < size; i++) {
			Hero hero = maxFightingHeros.get(i);
			if (hero == null) {
				continue;
			}

			int modelId = hero.getModeId();
			if (mainRoleModelId == modelId) {
				continue;
			}

			teamFighting += hero.getFighting();
			if (!heroModelIdList.contains(modelId)) {
				heroModelIdList.add(modelId);
			}
		}

		// System.err.println(new StrBuilder().append("没有任何自发阵容--最强五人：").append(player.getUserId()).append(",战斗力：").append(teamFighting).toString());
		return teamFighting;
	}

	/**
	 * 获取副本阵容中的战斗力
	 * 
	 * @param player
	 * @param heroModelIdList
	 * @return
	 */
	private int getCopyEmbattleTeamFighting(PlayerIF player, List<Integer> heroModelIdList) {
		EmbattlePositionInfo embattleInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.Normal_VALUE, EmBattlePositionKey.posCopy.getKey());
		if (embattleInfo == null) {
			return 0;
		}

		List<EmbattleHeroPosition> normalPosList = embattleInfo.getPos();
		if (normalPosList == null || normalPosList.isEmpty()) {
			return 0;
		}

		int teamFighting = 0;
		FSHeroMgr heroMgr = FSHeroMgr.getInstance();
		for (int i = 0, size = normalPosList.size(); i < size; i++) {
			EmbattleHeroPosition heroPos = normalPosList.get(i);
			FSHero hero = heroMgr.getHeroById(player, heroPos.getId());
			if (hero == null) {
				continue;
			}

			teamFighting += hero.getFighting();
			heroModelIdList.add(hero.getModeId());
		}

		// System.err.println(new StrBuilder().append("没有任何自发阵容--副本：").append(player.getUserId()).append(",战斗力：").append(teamFighting).toString());

		return teamFighting;
	}

	/**
	 * 获取所有的英雄Id列表
	 * 
	 * @param hero
	 * @return
	 */
	private List<Integer> getHeroModelIdList(List<HeroInfo> hero) {
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
	private Map<String, MatchUserInfo> getMatchUserInfo(String userId, int level, int maxLevel, int minFighting, int maxFighting, List<String> enemyIdList, List<String> hasUserIdList, RankType rankType) {
		Map<String, MatchUserInfo> matchUserInfo = new HashMap<String, MatchUserInfo>(AngelArrayConst.MIN_MATCH_SIZE);

		Ranking<AngelArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(rankType);
		if (ranking == null) {
			GameLog.error("从万仙阵阵容榜中匹配", userId, String.format("排行榜类型[%s]万仙阵阵容的排行榜是Null", rankType));
			return matchUserInfo;
		}

		AngelArrayComparable minValue = new AngelArrayComparable();
		minValue.setLevel(level);
		minValue.setFighting(minFighting);

		AngelArrayComparable maxValue = new AngelArrayComparable();
		maxValue.setLevel(maxLevel);
		maxValue.setFighting(maxFighting);

		SegmentList<? extends MomentRankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute>> segmentList = ranking.getSegmentList(minValue, maxValue);
		int refSize = segmentList.getRefSize();
		if (refSize <= 0) {
			GameLog.error("从万仙阵阵容榜中匹配", userId, String.format("需要匹配的战力上下限是[%s,%s]不能从类型为[%s]榜中截取到任何数据", minFighting, maxFighting, rankType));
			return matchUserInfo;
		}

		// StringBuilder sb = new StringBuilder();

		for (int i = 0; i < refSize; i++) {
			MomentRankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute> momentRankingEntry = segmentList.get(i);
			if (momentRankingEntry == null) {
				continue;
			}

			AngelArrayTeamInfoAttribute extendedAttribute = momentRankingEntry.getExtendedAttribute();
			String id = extendedAttribute.getUserId();
			if (enemyIdList.contains(id) || matchUserInfo.containsKey(id) || hasUserIdList.contains(id)) {
				continue;
			}

			TeamInfo teamInfo = extendedAttribute.getTeamInfo();
			if (teamInfo == null) {
				GameLog.error("从万仙阵阵容榜中匹配", userId, String.format("id为[%s]的用户在万仙阵匹配榜[%s]找不到对应的TeamInfo", id, rankType));
				continue;
			}

			matchUserInfo.put(id, new MatchUserInfo(id, teamInfo == null ? -1 : teamInfo.getCareer(), rankType, teamInfo.getTeamFighting()));

			if (matchUserInfo.size() >= AngelArrayConst.MIN_MATCH_SIZE) {
				break;
			}

			// sb.append("人Id：").append(id).append(",战力：").append(momentRankingEntry.getComparable().getFighting()).append("\n");
		}

		if (matchUserInfo.isEmpty()) {
			GameLog.error("从万仙阵阵容榜中匹配", userId, String.format("需要匹配的战力上下限是[%s,%s]不能从类型为[%s]榜中匹配到任何玩家数据", minFighting, maxFighting, rankType));
		}

		// System.err.println("第一步根据竞技场阵容随机到的人信息：" + sb.toString());
		return matchUserInfo;
	}

	/**
	 * 通过战力榜匹配数据
	 * 
	 * @param userId 匹配人的角色Id
	 * @param rankCount 需要从战力榜中补多少个出来
	 * @param hasMatchList 已经匹配到的数据
	 * @return
	 */
	private void getFightingRankMatch(String userId, int rankCount, int minFighting, int maxFighting, Map<String, MatchUserInfo> matchUserInfo, List<String> enemyIdList, List<String> hasUserIdList) {
		Ranking<FightingComparable, RankingLevelData> rank = RankingFactory.getRanking(SECOND_MATCH_RANK_TYPE);
		if (rank == null) {
			GameLog.error("通过战力榜匹配数据", SECOND_MATCH_RANK_TYPE.getName(), "获取不到对应某个排行榜的数据");
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
			GameLog.info("通过战力榜匹配数据", SECOND_MATCH_RANK_TYPE.getName(), String.format("匹配区间[%s,%s]中没有在排行榜中截取到数据", minFighting, maxFighting));
		}

		// StringBuilder sb = new StringBuilder();

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
			if (enemyIdList.contains(id) || matchUserInfo.containsKey(id) || hasUserIdList.contains(id)) {
				continue;
			}

			matchUserInfo.put(id, new MatchUserInfo(id, rankingLevelData.getCareerLevel(), SECOND_MATCH_RANK_TYPE, rankingLevelData.getFightingTeam()));
			matchCount++;
			nonPerson = false;

			if (matchCount >= rankCount) {
				break;
			}

			// sb.append("人Id：").append(id).append(",战力：").append(momentRankingEntry.getComparable().getFighting()).append("\n");
		}

		int offCount = rankCount - matchCount;
		if (offCount <= 0) {
			// System.err.println(String.format("第二步-全部人够了：%s", sb.toString()));
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
				if (enemyIdList.contains(id) || matchUserInfo.containsKey(id) || hasUserIdList.contains(id)) {
					continue;
				}

				matchUserInfo.put(id, new MatchUserInfo(id, rankingLevelData.getCareerLevel(), SECOND_MATCH_RANK_TYPE, rankingLevelData.getFightingTeam()));
				offCount--;
				nonPerson = false;

				if (offCount <= 0) {
					break;
				}

				// sb.append("向上浮动人Id：").append(id).append(",战力：").append(rankingEntry.getComparable().getFighting()).append("\n");
			}
		} else {
			GameLog.info("通过战力榜匹配数据", SECOND_MATCH_RANK_TYPE.getName(), String.format("战力[%s]上浮不到任何人", maxFighting));
		}

		// 已经够了
		if (offCount <= 0) {
			// System.err.println(String.format("第二步-上浮全部人够了：%s", sb.toString()));
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
				if (enemyIdList.contains(id) || matchUserInfo.containsKey(id) || hasUserIdList.contains(id)) {
					continue;
				}

				matchUserInfo.put(id, new MatchUserInfo(id, rankingLevelData.getCareerLevel(), SECOND_MATCH_RANK_TYPE, rankingLevelData.getFightingTeam()));
				offCount--;
				nonPerson = false;

				if (offCount <= 0) {
					break;
				}

				// sb.append("向下浮动人Id：").append(id).append(",战力：").append(rankingEntry.getComparable().getFighting()).append("\n");
			}
		} else {
			GameLog.info("通过战力榜匹配数据", SECOND_MATCH_RANK_TYPE.getName(), String.format("战力[%s]下浮不到任何人", minFighting));
		}

		// 匹配到最后都没从战力榜获取到人
		if (nonPerson) {
			GameLog.info("通过战力榜匹配数据", SECOND_MATCH_RANK_TYPE.getName(), "从战力榜中没有上下浮动到任何人");
		}

		// System.err.println(String.format("第二步-随机结尾：%s", sb.toString()));
	}

	/**
	 * 获取角色在竞技场中的排名
	 * 
	 * @param userId
	 * @return
	 */
	private int getArenaRankIndex(MatchUserInfo userInfo) {
		String userId = userInfo.getUserId();
		Ranking ranking = RankingFactory.getRanking(RankType.ARENA);
		if (ranking == null) {
			return -1;
		}
		return ranking.getRanking(userId);
	}
}

class MatchUserInfo {
	private final String userId;
	private final int career;
	private final RankType rankType;// 数据来源的排行榜
	private final int fighting;// 匹配到的时候的战斗力

	public MatchUserInfo(String userId, int career, RankType rankType, int fighting) {
		this.userId = userId;
		this.career = career;
		this.rankType = rankType;
		this.fighting = fighting;
	}

	public String getUserId() {
		return userId;
	}

	public int getCareer() {
		return career;
	}

	public RankType getRankType() {
		return rankType;
	}

	public int getFighting() {
		return fighting;
	}
}