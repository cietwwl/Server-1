package com.bm.robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.bm.arena.RobotCfgDAO;
import com.bm.arena.RobotEntryCfg;
import com.bm.arena.RobotHeroCfg;
import com.bm.arena.RobotHeroCfgDAO;
import com.playerdata.team.EquipInfo;
import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.SkillInfo;
import com.playerdata.team.TeamInfo;
import com.rwbase.common.RandomUtil;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;


public final class RobotHeroBuilder {

	
	public static TeamInfo buildOnlyHerosTeamInfo(int robotId){
		String robotIdStr = String.valueOf(robotId);
		RobotEntryCfg robotCfg = RobotCfgDAO.getInstance().getOnlyHerosRobotCfg(robotIdStr);
		
		RobotHeroCfg robotHeroCfg = getRandomRobotHeroCfg(robotCfg);		
		List<HeroInfo> heroList = new ArrayList<HeroInfo>();		
		if (robotHeroCfg != null) {
			List<Integer> roleModelIdList = new ArrayList<Integer>();
			String firstHeroId = robotHeroCfg.getFirstHeroId();
			if (!StringUtils.isEmpty(firstHeroId)) {
				Integer id = Integer.valueOf(firstHeroId);
				roleModelIdList.add(id);
				
			}
			String secondHeroId = robotHeroCfg.getSecondHeroId();
			if (!StringUtils.isEmpty(secondHeroId)) {
				Integer id = Integer.valueOf(secondHeroId);
				roleModelIdList.add(id);
				
			}
			String thirdHeroId = robotHeroCfg.getThirdHeroId();
			if (!StringUtils.isEmpty(thirdHeroId)) {
				Integer id = Integer.valueOf(thirdHeroId);
				roleModelIdList.add(id);
				
			}
			String fourthHeroId = robotHeroCfg.getFourthHeroId();
			if (!StringUtils.isEmpty(fourthHeroId)) {
				Integer id = Integer.valueOf(fourthHeroId);
				roleModelIdList.add(id);
				
			}
			String fifthHeroId = robotHeroCfg.getFifthHeroId();
			if (!StringUtils.isEmpty(fifthHeroId)) {
				Integer id = Integer.valueOf(fifthHeroId);
				roleModelIdList.add(id);				
			}			
			
			int[] level = robotCfg.getLevel();
			int mainRoleLevel = level[getRandomIndex(level.length)];
			for (Integer roleModelIdTmp : roleModelIdList) {
				HeroInfo hero = buildNonMainHeroInfo(roleModelIdTmp, robotCfg, mainRoleLevel);
				heroList.add(hero);
			}
		}
		
		TeamInfo teamInfo = new TeamInfo();
		teamInfo.setHero(heroList);
		return teamInfo;		
	}


	private static RobotHeroCfg getRandomRobotHeroCfg(RobotEntryCfg robotCfg) {
		List<String> groupIdList = robotCfg.getHeroGroupId();		
		
		int heroGroupIdIndex = getRandomIndex(groupIdList.size());		
		String heroGroupId = groupIdList.get(heroGroupIdIndex);
		List<RobotHeroCfg> heroCfgList = RobotHeroCfgDAO.getInstance().getRobotHeroCfg(heroGroupId );
		
		int randomIndex = getRandomIndex(heroCfgList.size());
		RobotHeroCfg robotHeroCfg = heroCfgList.get(randomIndex);
		return robotHeroCfg;
	}
	
	
	public static HeroInfo buildMainHeroInfo( int roleModelId, RobotEntryCfg robotCfg) {
		
		RobotHeroBuildData heroBuildData = RobotHeroBuildData.newMainHeroData(roleModelId, robotCfg);
		
		return buildHeroInfo(heroBuildData);
	}
	
	public static HeroInfo buildNonMainHeroInfo(int roleModelId, RobotEntryCfg robotCfg, int mainRoleLevel) {
		RobotHeroBuildData heroBuildData = RobotHeroBuildData.newHeroData(roleModelId, robotCfg, mainRoleLevel);
		
		return buildHeroInfo(heroBuildData);
	}
	
	
	
	/**
	 * 获取英雄的信息
	 * 
	 * @param robotCfg 机器人的配置
	 * @param isMainRole 是否是主角
	 * @param roleModelId 角色的模型Id
	 * @return
	 */
	private static HeroInfo buildHeroInfo(RobotHeroBuildData heroBuildData) {	
	
		
		HeroInfo heroInfo = new HeroInfo();


		// ----------------------------------------英雄基础属性
		HeroBaseInfo heroBaseIndo = newHeroBaseInfo(heroBuildData);
		heroInfo.setBaseInfo(heroBaseIndo);

		RoleCfgDAO roleCfgDAO = RoleCfgDAO.getInstance();
		// 模版Id
		String tmpId = heroBuildData.getTemplateId();
		RoleCfg roleCfg = roleCfgDAO.getCfgById(tmpId);		
		
		// ----------------------------------------装备
		List<EquipInfo> equipList = newEquipList(heroBuildData, roleCfg);
		heroInfo.setEquip(equipList);

		// ----------------------------------------宝石
		// 宝石数量
		ArrayList<String> gemList_ = newGemList(heroBuildData);
		heroInfo.setGem(gemList_);
		// ----------------------------------------技能
		// 技能
		List<SkillInfo> skillInfoList = new ArrayList<SkillInfo>();

		int heroLevel = heroBuildData.getHeroLevel();
		checkAndAddSKillInfo(skillInfoList,  heroLevel , roleCfg.getSkillId01(), heroBuildData.getHeroFirstSkillLevel());
		checkAndAddSKillInfo(skillInfoList,  heroLevel, roleCfg.getSkillId02(), heroBuildData.getHeroSecondSkillLevel());
		checkAndAddSKillInfo(skillInfoList,  heroLevel, roleCfg.getSkillId03(), heroBuildData.getHeroThirdSkillLevel());
		checkAndAddSKillInfo(skillInfoList,  heroLevel, roleCfg.getSkillId04(), heroBuildData.getHeroFourthSkillLevel());
		checkAndAddSKillInfo(skillInfoList,  heroLevel, roleCfg.getSkillId05(), heroBuildData.getHeroFifthSkillLevel());

		heroInfo.setSkill(skillInfoList);
		return heroInfo;
	}

	private static ArrayList<String> newGemList(RobotHeroBuildData heroBuildData) {
		
		int gemCount = heroBuildData.getGemCount();
		// 宝石等级
		int[] gemLevelArray = heroBuildData.getGemLevelArray();

		// ==宝石类型==
		int[] gemTypeArray = heroBuildData.getGemTypeArray();
		
		ArrayList<Integer> gemList = new ArrayList<Integer>();
		for (int a : gemTypeArray) {
			if (!gemList.contains(a)) {
				gemList.add(a);
			}
		}

		// ==随机宝石类型==
		if (gemCount < gemList.size()) {
			Collections.shuffle(gemList);
		} else {
			gemCount = gemList.size();
		}

		List<String> canGemList = new ArrayList<String>(gemCount);
		for (int i = 0; i < gemCount; i++) {
			String gemId = String.valueOf(gemList.remove(getRandomIndex( gemList.size())));
			canGemList.add(gemId);
		}

		ArrayList<String> gemList_ = new ArrayList<String>();
		GemCfgDAO gemCfgDAO = GemCfgDAO.getInstance();
		for (int i = 0, gemSize = canGemList.size(); i < gemSize; i++) {
			String nextGemId = canGemList.get(i).toString();
			int gemLevel = gemLevelArray[getRandomIndex(gemLevelArray.length)];
			for (int j = gemLevel; --j >= 0;) {
				GemCfg gemCfg = (GemCfg) gemCfgDAO.getCfgById(nextGemId);
				if (gemCfg == null) {
					continue;
				}

				String n = String.valueOf(gemCfg.getComposeItemID());
				if (!StringUtils.isEmpty(n)) {
					nextGemId = n;
				}
			}

			gemList_.add(nextGemId);
		}
		return gemList_;
	}

	private static List<EquipInfo> newEquipList(RobotHeroBuildData heroBuildData, RoleCfg roleCfg) {
		// 英雄的品质Id
		// 装备数量
		int heroEquipNum = heroBuildData.getHeroEquipNum();

		RoleQualityCfgDAO qualityCfgDAO = RoleQualityCfgDAO.getInstance();
		String qualityId = roleCfg.getQualityId();
		List<Integer> equipIdList = qualityCfgDAO.getEquipList(qualityId);// 可以穿戴的装备Id列表

		int size = equipIdList.size();
		List<Integer> canEquipIdList;
		if (size > 0 && heroEquipNum < size) {
			canEquipIdList = new ArrayList<Integer>(size);
			int startIndex = getRandomIndex(size);// 设置一个起点
			for (int i = startIndex; i < heroEquipNum; i++) {
				int index = i;
				if (index >= size) {
					index -= size;
				}

				Integer hasValue = equipIdList.get(index);
				if (hasValue != null) {
					canEquipIdList.add(hasValue);
				}
			}
		} else {
			canEquipIdList = equipIdList;
		}

		// 装备附灵
		int[] heroEnchant = heroBuildData.getHeroEnchant();

		int canSize = canEquipIdList.size();
		List<EquipInfo> equipList = new ArrayList<EquipInfo>(canSize);
		for (int i = 0; i < canSize; i++) {
			EquipInfo equipInfo = new EquipInfo();
			equipInfo.settId(canEquipIdList.get(i).toString());
			equipInfo.seteLevel(heroEnchant[getRandomIndex( heroEnchant.length)]);
			equipList.add(equipInfo);
		}
		return equipList;
	}

	private static HeroBaseInfo newHeroBaseInfo(RobotHeroBuildData heroBuildData) {
		HeroBaseInfo heroBaseIndo = new HeroBaseInfo();		
		heroBaseIndo.setLevel(heroBuildData.getHeroLevel());
		heroBaseIndo.setQuality(heroBuildData.getRoleModelId() + "_" + heroBuildData.getHeroQuality());
		heroBaseIndo.setStar(heroBuildData.getHeroStar());
		heroBaseIndo.setTmpId(heroBuildData.getTemplateId());
		return heroBaseIndo;
	}

	/**
	 * 
	 * @param skillInfoList
	 * @param r
	 * @param heroLevel
	 * @param skillId
	 * @param skillLevelArray
	 */
	private static void checkAndAddSKillInfo(List<SkillInfo> skillInfoList, int heroLevel, String skillId, int[] skillLevelArray) {
		// 技能Id
		if (StringUtils.isEmpty(skillId)) {
			return;
		}

		// 技能等级
		if (skillLevelArray == null) {
			return;
		}

		int skillLevel = skillLevelArray[getRandomIndex(skillLevelArray.length)];
		skillLevel = skillLevel > heroLevel ? heroLevel : skillLevel;

		if (skillLevel <= 0) {
			return;
		}

		SkillInfo skillInfo = new SkillInfo();
		skillInfo.setSkillId(skillId.split("_")[0] + "_" + skillLevel);
		skillInfo.setSkillLevel(skillLevel);

		skillInfoList.add(skillInfo);
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
}