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
import com.log.GameLog;
import com.playerdata.FightingCalculator;
import com.playerdata.army.ArmyMagic;
import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.SkillInfo;
import com.playerdata.team.TeamInfo;
import com.rwbase.common.RandomUtil;
import com.rwbase.common.attribute.AttributeBM;
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

		return getRobotTeamInfo(robotId, new BuildRoleInfo(userId, "NonMainRole", null, null, 0, roleModelIdList), false);
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

	//
	// public static HeroInfo buildMainHeroInfo(int roleModelId, RobotEntryCfg robotCfg) {
	//
	// RobotHeroBuildData heroBuildData = RobotHeroBuildData.newMainHeroData(roleModelId, robotCfg);
	//
	// return buildHeroInfo(heroBuildData);
	// }
	//
	// public static HeroInfo buildNonMainHeroInfo(int roleModelId, RobotEntryCfg robotCfg, int mainRoleLevel) {
	// RobotHeroBuildData heroBuildData = RobotHeroBuildData.newHeroData(roleModelId, robotCfg, mainRoleLevel);
	//
	// return buildHeroInfo(heroBuildData);
	// }
	//
	// /**
	// * 获取英雄的信息
	// *
	// * @param robotCfg 机器人的配置
	// * @param isMainRole 是否是主角
	// * @param roleModelId 角色的模型Id
	// * @return
	// */
	// private static HeroInfo buildHeroInfo(RobotHeroBuildData heroBuildData) {
	//
	// HeroInfo heroInfo = new HeroInfo();
	//
	// // ----------------------------------------英雄基础属性
	// HeroBaseInfo heroBaseIndo = newHeroBaseInfo(heroBuildData);
	// heroInfo.setBaseInfo(heroBaseIndo);
	//
	// RoleCfgDAO roleCfgDAO = RoleCfgDAO.getInstance();
	// // 模版Id
	// String tmpId = heroBuildData.getTemplateId();
	// RoleCfg roleCfg = roleCfgDAO.getCfgById(tmpId);
	//
	// // ----------------------------------------装备
	// List<EquipInfo> equipList = newEquipList(heroBuildData, roleCfg);
	// heroInfo.setEquip(equipList);
	//
	// // ----------------------------------------宝石
	// // 宝石数量
	// ArrayList<String> gemList_ = newGemList(heroBuildData);
	// heroInfo.setGem(gemList_);
	// // ----------------------------------------技能
	// // 技能
	// List<SkillInfo> skillInfoList = new ArrayList<SkillInfo>();
	//
	// int heroLevel = heroBuildData.getHeroLevel();
	// checkAndAddSKillInfo(skillInfoList, heroLevel, roleCfg.getSkillId01(), heroBuildData.getHeroFirstSkillLevel());
	// checkAndAddSKillInfo(skillInfoList, heroLevel, roleCfg.getSkillId02(), heroBuildData.getHeroSecondSkillLevel());
	// checkAndAddSKillInfo(skillInfoList, heroLevel, roleCfg.getSkillId03(), heroBuildData.getHeroThirdSkillLevel());
	// checkAndAddSKillInfo(skillInfoList, heroLevel, roleCfg.getSkillId04(), heroBuildData.getHeroFourthSkillLevel());
	// checkAndAddSKillInfo(skillInfoList, heroLevel, roleCfg.getSkillId05(), heroBuildData.getHeroFifthSkillLevel());
	//
	// heroInfo.setSkill(skillInfoList);
	// return heroInfo;
	// }
	//
	// private static ArrayList<String> newGemList(RobotHeroBuildData heroBuildData) {
	//
	// int gemCount = heroBuildData.getGemCount();
	// // 宝石等级
	// int[] gemLevelArray = heroBuildData.getGemLevelArray();
	//
	// // ==宝石类型==
	// int[] gemTypeArray = heroBuildData.getGemTypeArray();
	//
	// ArrayList<Integer> gemList = new ArrayList<Integer>();
	// for (int a : gemTypeArray) {
	// if (!gemList.contains(a)) {
	// gemList.add(a);
	// }
	// }
	//
	// // ==随机宝石类型==
	// if (gemCount < gemList.size()) {
	// Collections.shuffle(gemList);
	// } else {
	// gemCount = gemList.size();
	// }
	//
	// List<String> canGemList = new ArrayList<String>(gemCount);
	// for (int i = 0; i < gemCount; i++) {
	// String gemId = String.valueOf(gemList.remove(getRandomIndex(gemList.size())));
	// canGemList.add(gemId);
	// }
	//
	// ArrayList<String> gemList_ = new ArrayList<String>();
	// GemCfgDAO gemCfgDAO = GemCfgDAO.getInstance();
	// for (int i = 0, gemSize = canGemList.size(); i < gemSize; i++) {
	// String nextGemId = canGemList.get(i).toString();
	// int gemLevel = gemLevelArray[getRandomIndex(gemLevelArray.length)];
	// for (int j = gemLevel; --j >= 0;) {
	// GemCfg gemCfg = (GemCfg) gemCfgDAO.getCfgById(nextGemId);
	// if (gemCfg == null) {
	// continue;
	// }
	//
	// String n = String.valueOf(gemCfg.getComposeItemID());
	// if (!StringUtils.isEmpty(n)) {
	// nextGemId = n;
	// }
	// }
	//
	// gemList_.add(nextGemId);
	// }
	// return gemList_;
	// }
	//
	// private static List<EquipInfo> newEquipList(RobotHeroBuildData heroBuildData, RoleCfg roleCfg) {
	// // 英雄的品质Id
	// // 装备数量
	// int heroEquipNum = heroBuildData.getHeroEquipNum();
	//
	// RoleQualityCfgDAO qualityCfgDAO = RoleQualityCfgDAO.getInstance();
	// String qualityId = roleCfg.getQualityId();
	// List<Integer> equipIdList = qualityCfgDAO.getEquipList(qualityId);// 可以穿戴的装备Id列表
	//
	// int size = equipIdList.size();
	// List<Integer> canEquipIdList;
	// if (size > 0 && heroEquipNum < size) {
	// canEquipIdList = new ArrayList<Integer>(size);
	// int startIndex = getRandomIndex(size);// 设置一个起点
	// for (int i = startIndex; i < heroEquipNum; i++) {
	// int index = i;
	// if (index >= size) {
	// index -= size;
	// }
	//
	// Integer hasValue = equipIdList.get(index);
	// if (hasValue != null) {
	// canEquipIdList.add(hasValue);
	// }
	// }
	// } else {
	// canEquipIdList = equipIdList;
	// }
	//
	// // 装备附灵
	// int[] heroEnchant = heroBuildData.getHeroEnchant();
	//
	// int canSize = canEquipIdList.size();
	// List<EquipInfo> equipList = new ArrayList<EquipInfo>(canSize);
	// for (int i = 0; i < canSize; i++) {
	// EquipInfo equipInfo = new EquipInfo();
	// equipInfo.settId(canEquipIdList.get(i).toString());
	// equipInfo.seteLevel(heroEnchant[getRandomIndex(heroEnchant.length)]);
	// equipList.add(equipInfo);
	// }
	// return equipList;
	// }
	//
	// private static HeroBaseInfo newHeroBaseInfo(RobotHeroBuildData heroBuildData) {
	// HeroBaseInfo heroBaseIndo = new HeroBaseInfo();
	// heroBaseIndo.setLevel(heroBuildData.getHeroLevel());
	// heroBaseIndo.setQuality(heroBuildData.getRoleModelId() + "_" + heroBuildData.getHeroQuality());
	// heroBaseIndo.setStar(heroBuildData.getHeroStar());
	// heroBaseIndo.setTmpId(heroBuildData.getTemplateId());
	// return heroBaseIndo;
	// }
	//
	// /**
	// *
	// * @param skillInfoList
	// * @param r
	// * @param heroLevel
	// * @param skillId
	// * @param skillLevelArray
	// */
	// private static void checkAndAddSKillInfo(List<SkillInfo> skillInfoList, int heroLevel, String skillId, int[] skillLevelArray) {
	// // 技能Id
	// if (StringUtils.isEmpty(skillId)) {
	// return;
	// }
	//
	// // 技能等级
	// if (skillLevelArray == null) {
	// return;
	// }
	//
	// int skillLevel = skillLevelArray[getRandomIndex(skillLevelArray.length)];
	// skillLevel = skillLevel > heroLevel ? heroLevel : skillLevel;
	//
	// if (skillLevel <= 0) {
	// return;
	// }
	//
	// SkillInfo skillInfo = new SkillInfo();
	// skillInfo.setSkillId(skillId.split("_")[0] + "_" + skillLevel);
	// skillInfo.setSkillLevel(skillLevel);
	//
	// skillInfoList.add(skillInfo);
	// }
	//
	/**
	 * 获取到随机的索引
	 *
	 * @param len
	 * @return
	 */
	private static int getRandomIndex(int len) {
		return RandomUtil.nextInt(len);
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
		RobotEntryCfg angelRobotCfg = RobotCfgDAO.getInstance().getRobotCfg(String.valueOf(robotId));
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
			GameLog.error("生成机器人", "未知角色Id", String.format("[%s]机器人阵容组合找不到RobotHeroCfg", heroTeamId));
			return null;
		}

		RobotHeroCfg heroCfg = heroCfgList.get(RobotHelper.getRandomIndex(r, heroCfgList.size()));
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

		StringBuilder sb = new StringBuilder();
		sb.append(roleCfg.getModelId()).append("_").append(System.currentTimeMillis());// 模拟生成一个角色Id，modelId_时间

		String headImage;
		if (sex == ESex.Men.getOrder()) {
			headImage = "10001";
		} else {
			headImage = "10002";
		}

		return getRobotTeamInfo(robotId, new BuildRoleInfo(sb.toString(), name, headImage, "", career, heroTmpIdList), true);
	}

	/**
	 * 获取机器人
	 * 
	 * @param robotId
	 * @param roleInfo
	 * @return
	 */
	public static TeamInfo getRobotTeamInfo(int robotId, BuildRoleInfo roleInfo, boolean needMainRole) {
		RobotEntryCfg robotCfg = RobotCfgDAO.getInstance().getRobotCfg(String.valueOf(robotId));
		if (robotCfg == null) {
			return null;
		}

		String userId = roleInfo == null ? null : roleInfo.getUserId();
		List<Integer> heroTmpIdList = roleInfo == null ? null : roleInfo.getHeroTmpIdList();

		// if (StringUtils.isEmpty(robotName)) {
		// return null;
		// }

		int[] level = robotCfg.getLevel();
		int mainRoleLevel = level[RobotHelper.getRandomIndex(new Random(), level.length)];

		// 阵容信息
		TeamInfo teamInfo = new TeamInfo();
		if (needMainRole) {
			// ----------------------------------------主角信息
			buildMainRole(robotCfg, teamInfo, roleInfo, mainRoleLevel);
		}

		// ----------------------------------------额外属性
		teamInfo.setExtraId(robotCfg.getExtraAttrId());
		// ----------------------------------------法宝信息
		ArmyMagic magic = buildMagic(robotCfg, mainRoleLevel);
		teamInfo.setMagic(magic);
		int finalMagicId = magic.getModelId();
		int magicLevel = magic.getLevel();

		// 补阵容机制，不够5人的情况下，就直接从机器人当中随机需要的个数出来
		checkHeroSize(heroTmpIdList, robotCfg);

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
	 * 构造一个主角
	 * 
	 * @param robotCfg
	 * @param teamInfo
	 * @param roleInfo
	 * @param mainRoleLeve
	 * 
	 * @return 返回构造的主角的等级
	 */
	private static int buildMainRole(RobotEntryCfg robotCfg, TeamInfo teamInfo, BuildRoleInfo roleInfo, int mainRoleLevel) {
		String userId = roleInfo == null ? null : roleInfo.getUserId();
		String robotName = roleInfo == null ? null : roleInfo.getRobotName();
		String groupName = roleInfo == null ? null : roleInfo.getGroupName();
		String headId = roleInfo == null ? null : roleInfo.getHeadId();
		int career = roleInfo == null ? 0 : roleInfo.getCareer();

		Random r = new Random();
		// ----------------------------------------主角基础信息
		// Vip等级
		int[] vipLevel = robotCfg.getVipLevel();
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
	private static ArmyMagic buildMagic(RobotEntryCfg robotCfg, int mainRoleLevel) {
		Random r = new Random();
		ArmyMagic magicInfo = new ArmyMagic();
		// 法宝Id
		int[] magicId = robotCfg.getMagicId();
		int finalMagicId = magicId[RobotHelper.getRandomIndex(r, magicId.length)];
		magicInfo.setModelId(finalMagicId);
		// 法宝等级
		int[] magicLevelArray = robotCfg.getMagicLevel();
		int magicLevel = magicLevelArray[RobotHelper.getRandomIndex(r, magicLevelArray.length)];
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

	public static class BuildRoleInfo
	{
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