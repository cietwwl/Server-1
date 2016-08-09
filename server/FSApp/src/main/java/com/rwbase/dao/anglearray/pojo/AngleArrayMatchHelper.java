package com.rwbase.dao.anglearray.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.util.StringUtils;

import com.bm.arena.ArenaBM;
import com.bm.arena.RobotCfgDAO;
import com.bm.arena.RobotEntryCfg;
import com.bm.arena.RobotHelper;
import com.bm.arena.RobotHeroCfg;
import com.bm.arena.RobotHeroCfgDAO;
import com.bm.arena.RobotManager;
import com.bm.rank.RankType;
import com.bm.rank.anglearray.AngleArrayComparable;
import com.bm.rank.fightingAll.FightingComparable;
import com.bm.rank.teaminfo.AngelArrayTeamInfoAttribute;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.common.Weight;
import com.log.GameLog;
import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.PlayerMgr;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyMagic;
import com.playerdata.readonly.PlayerIF;
import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.SkillInfo;
import com.playerdata.team.TeamInfo;
import com.rw.fsutil.common.SegmentList;
import com.rw.fsutil.ranking.MomentRankingEntry;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.enu.ECareer;
import com.rwbase.common.enu.ESex;
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
						teamFighting = AngelArrayTeamInfoHelper.getTeamInfoHeroModelListById(readOnlyPlayer, atkHeroList, heroModelIdList);
						hasTeam = true;
					} else {
						// 再找防守阵容
						List<String> heroIdList = arenaData.getHeroIdList();// 防守阵容信息
						if (heroIdList != null && !heroIdList.isEmpty() && readOnlyPlayer != null) {
							teamFighting = AngelArrayTeamInfoHelper.getTeamInfoHeroModelListByUUIDList(readOnlyPlayer, heroIdList, heroModelIdList);
							hasTeam = true;
						}
					}
				}

				if (readOnlyPlayer != null) {// 从角色身上取阵容
					if (!hasTeam) {
//						HeroMgrIF heroMgr = readOnlyPlayer.getHeroMgr();
						HeroMgr heroMgr = readOnlyPlayer.getHeroMgr();

						int mainRoleModelId = readOnlyPlayer.getModelId();

						heroModelIdList.add(mainRoleModelId);
						teamFighting += readOnlyPlayer.getMainRoleHero().getFighting();

//						List<? extends HeroIF> maxFightingHeros = heroMgr.getMaxFightingHeros();
						List<? extends Hero> maxFightingHeros = heroMgr.getMaxFightingHeros(readOnlyPlayer);
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
							heroModelIdList.add(modelId);
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
					finalTeamInfo = getRobotTeamInfo(robotId);
				} else {
					finalTeamInfo = getRobotTeamInfo(robotId, ranResult, playerName, headId, groupName, career, heroModelIdList);
				}
			} else {
				if (!hasTeam) {
					finalTeamInfo = getRobotTeamInfo(robotId);
				} else {
					finalTeamInfo = getRobotTeamInfo(robotId, ranResult, playerName, headId, groupName, career, heroModelIdList);
				}
			}
		} else {
			finalTeamInfo = getRobotTeamInfo(robotId);
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
	 * 生成一个机器人
	 * 
	 * @param robotId
	 * @return
	 */
	public static ArmyInfo getRobotArmyInfo(int robotId, ArmyInfo armyInfo) {
		return null;
	}

	private static final String[] fNameArr = { "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "胡", "司马", "欧阳", "裴", "戚", "西门", "朴" };
	private static final String[] sNameArr = { "豆儿", "菲菲", "正熙", "仲基", "吹水", "月云", "雨", "雪", "雅莉", "永志", "诗涵", "紫琼", "敏之", "雨涵", "冰" };

	/**
	 * 获取角色信息
	 * 
	 * @param robotId
	 * @return
	 */
	public static TeamInfo getRobotTeamInfo(int robotId) {
		RobotEntryCfg angelRobotCfg = RobotCfgDAO.getInstance().getAngelRobotCfg(String.valueOf(robotId));
		if (angelRobotCfg == null) {
			return null;
		}

		Random r = new Random();
		// 随机一个职业
		ECareer[] values = ECareer.values();
		ECareer careerType = values[RobotHelper.getRandomIndex(r, values.length)];
		// 获取性别
		int sex = RobotHelper.getRandomIndex(r, 2);
		// 星级
		int[] starArr = angelRobotCfg.getStar();
		int star = starArr[RobotHelper.getRandomIndex(starArr.length)];
		// 获取英雄模版
		int career = careerType.getValue();
		RoleCfg roleCfg = RoleCfgDAO.getInstance().GetConfigBySexCareer(sex, career, star);
		if (roleCfg == null) {
			return null;
		}
		// 默认给个名字
		String name = fNameArr[RobotHelper.getRandomIndex(r, fNameArr.length)] + sNameArr[RobotHelper.getRandomIndex(r, sNameArr.length)];

		List<Integer> heroTmpIdList = new ArrayList<Integer>();
		heroTmpIdList.add(roleCfg.getModelId());
		// 阵容组合
		List<String> heroGroupId = angelRobotCfg.getHeroGroupId();
		String heroTeamId = heroGroupId.get(RobotHelper.getRandomIndex(r, heroGroupId.size()));

		List<RobotHeroCfg> heroCfgList = RobotHeroCfgDAO.getInstance().getRobotHeroCfg(heroTeamId);
		if (heroCfgList == null) {
			GameLog.error("万仙阵生成机器人", "未知角色Id", String.format("[%s]机器人阵容组合找不到RobotHeroCfg", heroTeamId));
			return null;
		}

		RobotHeroCfg heroCfg = heroCfgList.get(RobotHelper.getRandomIndex(r, heroCfgList.size()));
		if (heroCfg == null) {
			GameLog.error("万仙阵生成机器人", "未知角色Id", String.format("[%s]机器人阵容组合找不到RobotHeroCfg", heroTeamId));
			return null;
		}

		String firstHeroId = heroCfg.getFirstHeroId();
		if (!StringUtils.isEmpty(firstHeroId)) {
			heroTmpIdList.add(Integer.valueOf(firstHeroId));
		}

		String secondHeroId = heroCfg.getSecondHeroId();
		if (!StringUtils.isEmpty(secondHeroId)) {
			heroTmpIdList.add(Integer.valueOf(secondHeroId));
		}

		String thirdHeroId = heroCfg.getThirdHeroId();
		if (!StringUtils.isEmpty(thirdHeroId)) {
			heroTmpIdList.add(Integer.valueOf(thirdHeroId));
		}

		String fourthHeroId = heroCfg.getFourthHeroId();
		if (!StringUtils.isEmpty(fourthHeroId)) {
			heroTmpIdList.add(Integer.valueOf(fourthHeroId));
		}

		StringBuilder sb = new StringBuilder();
		sb.append(roleCfg.getModelId()).append("_").append(System.currentTimeMillis());// 模拟生成一个角色Id，modelId_时间

		String headImage;
		if (sex == ESex.Men.getOrder()) {
			headImage = "10001";
		} else {
			headImage = "10002";
		}
		return getRobotTeamInfo(robotId, sb.toString(), name, headImage, "", career, heroTmpIdList);
	}

	/**
	 * 获取万仙阵的机器人
	 * 
	 * @param robotId
	 * @param robotName
	 * @param career 职业
	 * @param heroTmpIdList List中用的<E>是{@link RoleCfg#getModelId()}
	 * @return
	 */
	public static TeamInfo getRobotTeamInfo(int robotId, String userId, String robotName, String headId, String groupName, int career, List<Integer> heroTmpIdList) {
		RobotEntryCfg angelRobotCfg = RobotCfgDAO.getInstance().getAngelRobotCfg(String.valueOf(robotId));
		if (angelRobotCfg == null) {
			return null;
		}

		if (StringUtils.isEmpty(robotName)) {
			return null;
		}

		if (heroTmpIdList == null || heroTmpIdList.isEmpty()) {
			return null;
		}

		Random r = new Random();
		// 主角的等级
		int[] level = angelRobotCfg.getLevel();
		int mainRoleLevel = level[RobotHelper.getRandomIndex(r, level.length)];

		// 阵容信息
		TeamInfo teamInfo = new TeamInfo();
		// ----------------------------------------主角基础信息
		// Vip等级
		int[] vipLevel = angelRobotCfg.getVipLevel();
		teamInfo.setVip(vipLevel[RobotHelper.getRandomIndex(r, vipLevel.length)]);
		// 名字
		teamInfo.setName(robotName);
		// 设置职业
		teamInfo.setCareer(career);
		// 设置帮派名字
		teamInfo.setGroupName(groupName);
		// 设置头像Id
		teamInfo.setHeadId(headId);
		// 设置等级
		teamInfo.setLevel(mainRoleLevel);
		// 设置角色的Id
		teamInfo.setUuid(userId);

		// ----------------------------------------主角法宝
		ArmyMagic magicInfo = new ArmyMagic();
		// 法宝Id
		int[] magicId = angelRobotCfg.getMagicId();
		int finalMagicId = magicId[RobotHelper.getRandomIndex(r, magicId.length)];
		magicInfo.setModelId(finalMagicId);
		// 法宝等级
		int[] magicLevelArray = angelRobotCfg.getMagicLevel();
		int magicLevel = magicLevelArray[RobotHelper.getRandomIndex(r, magicLevelArray.length)];
		magicLevel = magicLevel > mainRoleLevel ? mainRoleLevel : magicLevel;
		magicInfo.setLevel(magicLevel);
		teamInfo.setMagic(magicInfo);

		// 补阵容机制，不够5人的情况下，就直接从机器人当中随机需要的个数出来
		checkHeroSize(heroTmpIdList, r, angelRobotCfg);

		int heroSize = heroTmpIdList.size();
		List<HeroInfo> heroInfoList = new ArrayList<HeroInfo>(heroSize);

		int fighting = 0;
		RoleCfgDAO roleCfgDAO = RoleCfgDAO.getInstance();

		int mainRoleIndex = -1;

		for (int i = 0; i < heroSize; i++) {
			int heroModelId = heroTmpIdList.get(i);
			RoleCfg roleCfg = roleCfgDAO.getRoleCfgByModelId(heroModelId);
			if (roleCfg == null) {
				continue;
			}

			boolean isMainRole = roleCfg.getRoleType() == 1;
			HeroInfo heroInfo = getHeroInfo(angelRobotCfg, isMainRole, heroModelId, mainRoleLevel);
			if (heroInfo != null) {
				int heroPos = 0;
				if (isMainRole) {
					mainRoleIndex = i;
				} else {
					heroPos = mainRoleIndex == -1 ? i + 1 : i;
				}

				heroInfo.getBaseInfo().setPos(heroPos);
				heroInfoList.add(heroInfo);

				int skillLevel = 0;
				List<SkillInfo> skill = heroInfo.getSkill();
				for (int j = 0, skillSize = skill.size(); j < skillSize; j++) {
					SkillInfo skillInfo = skill.get(j);
					if (skillInfo == null) {
						continue;
					}

					int sLevel = skillInfo.getSkillLevel();
					if (sLevel > 0) {
						skillLevel += sLevel;
					}
				}

				// 战力
				int calFighting = FightingCalculator.calFighting(heroInfo.getBaseInfo().getTmpId(), skillLevel, isMainRole ? magicLevel : 0, isMainRole ? String.valueOf(finalMagicId) : "",
					AttributeBM.getRobotAttrData(userId, heroInfo, teamInfo));
				fighting += calFighting;
			}
		}

		teamInfo.setHero(heroInfoList);
		teamInfo.setTeamFighting(fighting);

		return teamInfo;
	}

	/**
	 * 检查英雄的数量
	 * 
	 * @param heroTmpIdList
	 * @param r
	 * @param angelRobotCfg
	 */
	private static void checkHeroSize(List<Integer> heroTmpIdList, Random r, RobotEntryCfg angelRobotCfg) {
		int heroSize = heroTmpIdList.size();
		if (heroSize < 5) {
			int needSize = 5 - heroSize;// 需要随机的数量
			List<String> heroGroupId = angelRobotCfg.getHeroGroupId();
			String groupId = heroGroupId.get(RobotHelper.getRandomIndex(r, heroGroupId.size()));
			List<RobotHeroCfg> robotHeroCfgList = RobotHeroCfgDAO.getInstance().getRobotHeroCfg(groupId);
			if (robotHeroCfgList != null && !robotHeroCfgList.isEmpty()) {
				RobotHeroCfg robotHeroCfg = robotHeroCfgList.get(RobotHelper.getRandomIndex(r, robotHeroCfgList.size()));
				if (robotHeroCfg != null) {
					List<Integer> heroIdList = new ArrayList<Integer>();
					String firstHeroId = robotHeroCfg.getFirstHeroId();
					if (!StringUtils.isEmpty(firstHeroId)) {
						Integer id = Integer.valueOf(firstHeroId);
						if (!heroTmpIdList.contains(id)) {
							heroIdList.add(id);
						}
					}
					String secondHeroId = robotHeroCfg.getSecondHeroId();
					if (!StringUtils.isEmpty(secondHeroId)) {
						Integer id = Integer.valueOf(secondHeroId);
						if (!heroTmpIdList.contains(id)) {
							heroIdList.add(id);
						}
					}
					String thirdHeroId = robotHeroCfg.getThirdHeroId();
					if (!StringUtils.isEmpty(thirdHeroId)) {
						Integer id = Integer.valueOf(thirdHeroId);
						if (!heroTmpIdList.contains(id)) {
							heroIdList.add(id);
						}
					}
					String fourthHeroId = robotHeroCfg.getFourthHeroId();
					if (!StringUtils.isEmpty(fourthHeroId)) {
						Integer id = Integer.valueOf(fourthHeroId);
						if (!heroTmpIdList.contains(id)) {
							heroIdList.add(id);
						}
					}

					int canRanSize = heroIdList.size();
					if (needSize >= canRanSize) {// 如果需要的超过了就直接把列表添加进去
						heroTmpIdList.addAll(heroIdList);
					} else {
						for (int i = 0; i < needSize; i++) {
							Integer hasValue = heroIdList.remove(RobotHelper.getRandomIndex(r, heroIdList.size()));
							if (hasValue != null) {
								heroTmpIdList.add(hasValue);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 获取英雄的信息
	 * 
	 * @param angelRobotCfg 机器人的配置
	 * @param isMainRole 是否是主角
	 * @param roleModelId 角色的模型Id
	 * @param mainRoleLevel 主角的等级
	 * @return
	 */
	private static HeroInfo getHeroInfo(RobotEntryCfg angelRobotCfg, boolean isMainRole, int roleModelId, int mainRoleLevel) {
		HeroInfo heroInfo = new HeroInfo();
		// ----------------------------------------英雄基础属性
		HeroBaseInfo baseInfo = RobotHelper.getRobotHeroBaseInfo(roleModelId, mainRoleLevel, isMainRole, angelRobotCfg);
		heroInfo.setBaseInfo(baseInfo);
		// 获取RoleCfg配置
		RoleCfgDAO roleCfgDAO = RoleCfgDAO.getInstance();
		RoleCfg roleCfg = roleCfgDAO.getCfgById(baseInfo.getTmpId());
		// ----------------------------------------装备
		heroInfo.setEquip(RobotHelper.getRobotEquipList(isMainRole, roleCfg, angelRobotCfg));
		// ----------------------------------------宝石
		heroInfo.setGem(RobotHelper.getRobotGemList(isMainRole, angelRobotCfg));
		// ----------------------------------------技能
		heroInfo.setSkill(RobotHelper.getRobotSkillInfoList(baseInfo.getLevel(), isMainRole, roleCfg, angelRobotCfg));
		// ----------------------------------------羁绊
		heroInfo.setFetters(RobotHelper.parseHeroFettersInfo(roleModelId, RobotManager.getRobotFettersInfo(angelRobotCfg)));
		// ----------------------------------------神器
		heroInfo.setFixEquip(RobotHelper.parseFixInfo(roleModelId, RobotManager.getRobotFixInfo(angelRobotCfg)));
		return heroInfo;
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