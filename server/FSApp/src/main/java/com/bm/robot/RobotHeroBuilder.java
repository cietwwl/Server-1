package com.bm.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.util.StringUtils;

import com.bm.arena.RobotCfgDAO;
import com.bm.arena.RobotEntryCfg;
import com.bm.arena.RobotHelper;
import com.bm.arena.RobotHeroCfg;
import com.bm.arena.RobotHeroCfgDAO;
import com.bm.arena.RobotManager;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.bm.robot.cfg.RobotFNameCfgDAO;
import com.bm.robot.cfg.RobotSNameCfgDAO;
import com.log.GameLog;
import com.playerdata.army.ArmyMagic;
import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.TeamInfo;
import com.rwbase.common.RandomUtil;
import com.rwbase.common.enu.ECareer;
import com.rwbase.common.enu.ESex;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

public final class RobotHeroBuilder {

	public static TeamInfo buildOnlyHerosTeamInfo(int robotId) {
		String robotIdStr = String.valueOf(robotId);
		RobotEntryCfg robotCfg = RobotCfgDAO.getInstance().getRobotCfg(robotIdStr);

		// 生成英雄的信息
		String userId = null;
		RobotHeroCfg robotHeroCfg = getRandomRobotHeroCfg(robotCfg);

		List<Integer> roleModelIdList = new ArrayList<Integer>(5);
		if (robotHeroCfg != null) {
			String firstHeroId = robotHeroCfg.getFirstHeroId();
			if (!StringUtils.isEmpty(firstHeroId)) {
				Integer id = Integer.valueOf(firstHeroId);
				roleModelIdList.add(id);

				userId = firstHeroId;
			}

			String secondHeroId = robotHeroCfg.getSecondHeroId();
			if (!StringUtils.isEmpty(secondHeroId)) {
				Integer id = Integer.valueOf(secondHeroId);
				roleModelIdList.add(id);

				userId = secondHeroId;
			}

			String thirdHeroId = robotHeroCfg.getThirdHeroId();
			if (!StringUtils.isEmpty(thirdHeroId)) {
				Integer id = Integer.valueOf(thirdHeroId);
				roleModelIdList.add(id);

				userId = thirdHeroId;
			}

			String fourthHeroId = robotHeroCfg.getFourthHeroId();
			if (!StringUtils.isEmpty(fourthHeroId)) {
				Integer id = Integer.valueOf(fourthHeroId);
				roleModelIdList.add(id);

				userId = fourthHeroId;
			}

			String fifthHeroId = robotHeroCfg.getFifthHeroId();
			if (!StringUtils.isEmpty(fifthHeroId)) {
				Integer id = Integer.valueOf(fifthHeroId);
				roleModelIdList.add(id);

				userId = fifthHeroId;
			}
		}

		return getRobotTeamInfo(new BuildRoleInfo(userId, "NonMainRole", null, null, 0, roleModelIdList), false, RandomData.newInstance(robotId));
	}

	private static RobotHeroCfg getRandomRobotHeroCfg(RobotEntryCfg robotCfg) {
		List<String> groupIdList = robotCfg.getHeroGroupId();

		int heroGroupIdIndex = getRandomIndex(groupIdList.size());
		String heroGroupId = groupIdList.get(heroGroupIdIndex);
		List<RobotHeroCfg> heroCfgList = RobotHeroCfgDAO.getInstance().getRobotHeroCfg(heroGroupId);

		int randomIndex = getRandomIndex(heroCfgList.size());
		RobotHeroCfg robotHeroCfg = heroCfgList.get(randomIndex);
		return robotHeroCfg;
	}

	/**
	 * 获取到随机的索引
	 *
	 * @param len
	 * @return
	 */
	private static int getRandomIndex(int len) {
		return RandomUtil.nextInt(len);
	}

	// private static final String[] fNameArr = { "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "胡", "司马", "欧阳", "裴", "戚", "西门", "朴" };
	// private static final String[] sNameArr = { "豆儿", "菲菲", "正熙", "仲基", "吹水", "月云", "雨", "雪", "雅莉", "永志", "诗涵", "紫琼", "敏之", "雨涵", "冰" };

	/**
	 * 获取角色信息
	 * 
	 * @param robotId
	 * @return
	 */
	public static TeamInfo getRobotTeamInfo(int robotId) {

		return getRobotTeamInfo(RandomData.newInstance(robotId));
	}

	/**
	 * 获取角色信息
	 * 
	 * @param robotId
	 * @return
	 */
	public static TeamInfo getRobotTeamInfo(RandomData randomData) {
		RobotEntryCfg angelRobotCfg = RobotCfgDAO.getInstance().getRobotCfg(randomData.getRobotId());
		if (angelRobotCfg == null) {
			return null;
		}

		// 随机一个职业
		ECareer[] values = ECareer.values();
		ECareer careerType = values[randomData.getCareer(values.length)];
		// 获取性别
		int sex = randomData.getSex(2);
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
		String fName = RobotFNameCfgDAO.getInstance().get(randomData.getfName(RobotFNameCfgDAO.getInstance().getSize()));
		String sName = RobotSNameCfgDAO.getInstance().get(randomData.getsName(RobotSNameCfgDAO.getInstance().getSize()));
		String name = fName + sName;

		List<Integer> heroTmpIdList = getHeroIdList(angelRobotCfg, roleCfg, randomData);

		String robotUserId = randomData.getRobotUserId(roleCfg);

		String headImage;
		if (sex == ESex.Men.getOrder()) {
			headImage = "10001";
		} else {
			headImage = "10002";
		}

		BuildRoleInfo roleInfo = new BuildRoleInfo(robotUserId, name, headImage, "", career, heroTmpIdList);

		return getRobotTeamInfo(roleInfo, true, randomData);
	}

	private static List<Integer> getHeroIdList(RobotEntryCfg angelRobotCfg, RoleCfg roleCfg, RandomData randomData) {

		List<Integer> heroTmpIdList = new ArrayList<Integer>();
		heroTmpIdList.add(roleCfg.getModelId());
		// 阵容组合
		List<String> heroGroupId = angelRobotCfg.getHeroGroupId();
		String heroTeamId = heroGroupId.get(randomData.getHeroTeam(heroGroupId.size()));

		List<RobotHeroCfg> heroCfgList = RobotHeroCfgDAO.getInstance().getRobotHeroCfg(heroTeamId);
		if (heroCfgList == null) {
			GameLog.error("生成机器人", "未知角色Id", String.format("[%s]机器人阵容组合找不到RobotHeroCfg", heroTeamId));
			return null;
		}

		RobotHeroCfg heroCfg = heroCfgList.get(randomData.getHeroCfg(heroCfgList.size()));
		if (heroCfg == null) {
			GameLog.error("生成机器人", "未知角色Id", String.format("[%s]机器人阵容组合找不到RobotHeroCfg", heroTeamId));
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
		return heroTmpIdList;
	}

	/**
	 * 获取机器人
	 * 
	 * @param robotId
	 * @param roleInfo
	 * @return
	 */
	public static TeamInfo getRobotTeamInfo(int robotId, BuildRoleInfo roleInfo, boolean needMainRole) {
		return getRobotTeamInfo(roleInfo, needMainRole, RandomData.newInstance(robotId));
	}

	/**
	 * 获取机器人
	 * 
	 * @param robotId
	 * @param roleInfo
	 * @return
	 */
	public static TeamInfo getRobotTeamInfo(BuildRoleInfo roleInfo, boolean needMainRole, RandomData randomData) {
		RobotEntryCfg robotCfg = RobotCfgDAO.getInstance().getRobotCfg(randomData.getRobotId());
		if (robotCfg == null) {
			return null;
		}

		// String userId = roleInfo == null ? null : roleInfo.getUserId();
		List<Integer> heroTmpIdList = roleInfo == null ? null : roleInfo.getHeroTmpIdList();

		int[] level = robotCfg.getLevel();
		int mainRoleLevel = level[randomData.getMainRoleLevel(level.length)];

		// 阵容信息
		TeamInfo teamInfo = new TeamInfo();
		if (needMainRole) {
			// ----------------------------------------主角信息
			buildMainRole(robotCfg, teamInfo, roleInfo, mainRoleLevel, randomData);
		}

		// ----------------------------------------额外属性
		teamInfo.setExtraId(robotCfg.getExtraAttrId());
		// ----------------------------------------法宝信息
		ArmyMagic magic = buildMagic(robotCfg, mainRoleLevel, randomData);
		teamInfo.setMagic(magic);
		// int finalMagicId = magic.getModelId();
		// int magicLevel = magic.getLevel();

		// 检查是否有重复
		List<Integer> hasList = new ArrayList<Integer>(heroTmpIdList.size());
		for (int i = heroTmpIdList.size() - 1; i >= 0; --i) {
			int id = heroTmpIdList.get(i);

			if (hasList.contains(id)) {
				continue;
			}

			hasList.add(id);
		}

		// 补阵容机制，不够5人的情况下，就直接从机器人当中随机需要的个数出来
		if (randomData.isDoHeroMakeUp()) {
			checkHeroSize(hasList, robotCfg);
		}

		int heroSize = hasList.size();
		List<HeroInfo> heroInfoList = new ArrayList<HeroInfo>(heroSize);

		int fighting = 0;
		RoleCfgDAO roleCfgDAO = RoleCfgDAO.getInstance();

		int mainRoleIndex = -1;

		for (int i = 0; i < heroSize; i++) {
			int heroModelId = hasList.get(i);
			RoleCfg roleCfg = roleCfgDAO.getRoleCfgByModelId(heroModelId);
			if (roleCfg == null) {
				continue;
			}

			boolean isMainRole = roleCfg.getRoleType() == 1;
			HeroInfo heroInfo = getHeroInfo(robotCfg, isMainRole, heroModelId, mainRoleLevel);
			if (heroInfo != null) {
				int heroPos = 0;
				if (needMainRole) {
					if (isMainRole) {
						mainRoleIndex = i;
					} else {
						heroPos = mainRoleIndex == -1 ? i + 1 : i;
					}
				} else {
					heroPos = i;
				}

				heroInfo.getBaseInfo().setPos(heroPos);
				heroInfoList.add(heroInfo);

				// int skillLevel = 0;
				// List<SkillInfo> skill = heroInfo.getSkill();
				// for (int j = 0, skillSize = skill.size(); j < skillSize; j++) {
				// SkillInfo skillInfo = skill.get(j);
				// if (skillInfo == null) {
				// continue;
				// }
				//
				// int sLevel = skillInfo.getSkillLevel();
				// if (sLevel > 0) {
				// skillLevel += sLevel;
				// }
				// }

				// 战力
				// int calFighting = FightingCalculator.calFighting(heroInfo.getBaseInfo().getTmpId(), skillLevel, isMainRole ? magicLevel : 0, isMainRole ? String.valueOf(finalMagicId) : "",
				// AttributeBM.getRobotAttrData(userId, heroInfo, teamInfo));
				int calFighting = AngelArrayTeamInfoHelper.getInstance().calcRobotFighting(heroInfo, teamInfo);
				fighting += calFighting;
			}
		}

		teamInfo.setHero(heroInfoList);
		teamInfo.setTeamFighting(fighting);
		teamInfo.setRandomData(randomData);

		return teamInfo;
	}

	/**
	 * 构造一个主角
	 * 
	 * @param robotCfg
	 * @param teamInfo
	 * @param roleInfo
	 * @param mainRoleLeve
	 * 
	 * @return 返回构造的主角的等级
	 */
	private static int buildMainRole(RobotEntryCfg robotCfg, TeamInfo teamInfo, BuildRoleInfo roleInfo, int mainRoleLevel, RandomData randomData) {
		String userId = roleInfo == null ? null : roleInfo.getUserId();
		String robotName = roleInfo == null ? null : roleInfo.getRobotName();
		String groupName = roleInfo == null ? null : roleInfo.getGroupName();
		String headId = roleInfo == null ? null : roleInfo.getHeadId();
		int career = roleInfo == null ? 0 : roleInfo.getCareer();

		// ----------------------------------------主角基础信息
		// Vip等级
		int[] vipLevel = robotCfg.getVipLevel();
		teamInfo.setVip(vipLevel[randomData.getVipLevel(vipLevel.length)]);
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
		// 时装信息
		teamInfo.setFashion(RobotHelper.parseFashionInfo(RobotManager.getRobotFashionInfo(robotCfg)));
		// 道术信息
		teamInfo.setTaoist(RobotHelper.parseTaoist2Info(RobotManager.getRobotTaoistInfo(robotCfg)));

		return mainRoleLevel;
	}

	/**
	 * 构建一个法宝
	 * 
	 * @param robotCfg
	 * @param mainRoleLevel
	 * @return
	 */
	private static ArmyMagic buildMagic(RobotEntryCfg robotCfg, int mainRoleLevel, RandomData randomData) {
		ArmyMagic magicInfo = new ArmyMagic();
		// 法宝Id
		int[] magicId = robotCfg.getMagicId();
		int finalMagicId = magicId[randomData.getMagicId(magicId.length)];
		magicInfo.setModelId(finalMagicId);
		// 法宝等级
		int[] magicLevelArray = robotCfg.getMagicLevel();
		int magicLevel = magicLevelArray[randomData.getMagicLevel(magicLevelArray.length)];
		magicLevel = magicLevel > mainRoleLevel ? mainRoleLevel : magicLevel;
		magicInfo.setLevel(magicLevel);
		return magicInfo;
	}

	/**
	 * 检查英雄的数量
	 * 
	 * @param heroTmpIdList
	 * @param angelRobotCfg
	 */
	private static void checkHeroSize(List<Integer> heroTmpIdList, RobotEntryCfg angelRobotCfg) {
		Random r = new Random();

		int heroSize = heroTmpIdList == null ? 0 : heroTmpIdList.size();
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
					String fifthHeroId = robotHeroCfg.getFifthHeroId();
					if (!StringUtils.isEmpty(fifthHeroId)) {
						Integer id = Integer.valueOf(fifthHeroId);
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

	public static class BuildRoleInfo {
		private final String userId;// 角色ID
		private final String robotName;// 角色名字
		private final String headId;// 角色头像
		private final String groupName;// 帮派名字
		private final int career;// 职业类型
		private final List<Integer> heroTmpIdList;

		public BuildRoleInfo(String userId, String robotName, String headId, String groupName, int career, List<Integer> heroTmpIdList) {
			this.userId = userId;
			this.robotName = robotName;
			this.headId = headId;
			this.groupName = groupName;
			this.career = career;
			this.heroTmpIdList = heroTmpIdList;
		}

		public String getUserId() {
			return userId;
		}

		public String getRobotName() {
			return robotName;
		}

		public String getHeadId() {
			return headId;
		}

		public String getGroupName() {
			return groupName;
		}

		public int getCareer() {
			return career;
		}

		public List<Integer> getHeroTmpIdList() {
			return heroTmpIdList;
		}
	}
}