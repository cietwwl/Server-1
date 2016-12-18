package com.bm.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.util.StringUtils;

import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.team.EquipInfo;
import com.playerdata.team.FashionInfo;
import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroFixEquipInfo;
import com.playerdata.team.SkillInfo;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.dao.arena.pojo.HeroFettersRobotInfo;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersBaseCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.dao.FettersConditionCfgDAO;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersBaseTemplate;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersConditionTemplate;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

/*
 * @author HC
 * @date 2016年7月14日 上午9:48:36
 * @Description 
 */
public class RobotHelper {

	/**
	 * 获取到随机的索引
	 * 
	 * @param len
	 * @return
	 */
	public static int getRandomIndex(int len) {
		if (len <= 1) {
			return 0;
		}

		Random r = new Random();
		return getRandomIndex(r, len);
	}

	/**
	 * 获取到随机的索引
	 * 
	 * @param len
	 * @return
	 */
	public static int getRandomIndex(Random r, int len) {
		return r.nextInt(len);
	}

	/**
	 * 获取机器人的基础数据
	 * 
	 * @param roleModelId
	 * @param mainRoleLevel
	 * @param isMainRole
	 * @param robotCfg
	 * @return
	 */
	public static HeroBaseInfo getRobotHeroBaseInfo(int roleModelId, int mainRoleLevel, boolean isMainRole, RobotEntryCfg robotCfg) {
		Random r = new Random();

		HeroBaseInfo heroBaseIndo = new HeroBaseInfo();
		// 等级
		int heroLevel = mainRoleLevel;
		if (!isMainRole) {
			int[] level = isMainRole ? robotCfg.getLevel() : robotCfg.getHeroLevel();
			heroLevel = level[getRandomIndex(r, level.length)];
			heroLevel = heroLevel > mainRoleLevel ? mainRoleLevel : heroLevel;
		}
		heroBaseIndo.setLevel(heroLevel);
		// 品质
		int[] quality = isMainRole ? robotCfg.getQuality() : robotCfg.getHeroQuality();
		int heroQuality = quality[getRandomIndex(r, quality.length)];
		heroBaseIndo.setQuality(roleModelId + "_" + heroQuality);
		// 星级
		int[] star = isMainRole ? robotCfg.getStar() : robotCfg.getHeroStar();
		int heroStar = star[getRandomIndex(r, star.length)];
		heroBaseIndo.setStar(heroStar);
		// 设置模版Id
		String tmpId = roleModelId + "_" + heroStar;
		heroBaseIndo.setTmpId(tmpId);

		return heroBaseIndo;
	}

	/**
	 * 获取机器人的装备
	 * 
	 * @param isMainRole
	 * @param roleCfg
	 * @param robotCfg
	 * @return
	 */
	public static ArrayList<EquipInfo> getRobotEquipList(boolean isMainRole, RoleCfg roleCfg, RobotEntryCfg robotCfg) {
		Random r = new Random();
		// 英雄的品质Id
		String qualityId = roleCfg.getQualityId();
		// 装备数量
		int[] equipNum = isMainRole ? robotCfg.getEquipments() : robotCfg.getHeroEquipments();
		int heroEquipNum = equipNum[getRandomIndex(r, equipNum.length)];

		List<Integer> equipIdList = RoleQualityCfgDAO.getInstance().getEquipList(qualityId);// 可以穿戴的装备Id列表

		int size = equipIdList.size();
		List<Integer> canEquipIdList;
		if (size > 0 && heroEquipNum < size) {
			canEquipIdList = new ArrayList<Integer>(size);
			int startIndex = r.nextInt(size);// 设置一个起点
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

		int[] heroEnchant = isMainRole ? robotCfg.getEnchant() : robotCfg.getHeroEnchant();// 装备附灵

		int canSize = canEquipIdList.size();
		ArrayList<EquipInfo> equipList = new ArrayList<EquipInfo>(canSize);
		for (int i = 0; i < canSize; i++) {
			EquipInfo equipInfo = new EquipInfo();
			equipInfo.settId(canEquipIdList.get(i).toString());
			equipInfo.seteLevel(heroEnchant[getRandomIndex(r, heroEnchant.length)]);
			equipList.add(equipInfo);
		}

		return equipList;
	}

	/**
	 * 生成机器人的技能
	 * 
	 * @param heroLevel
	 * @param isMainRole
	 * @param roleCfg
	 * @param robotCfg
	 * @return
	 */
	public static ArrayList<SkillInfo> getRobotSkillInfoList(int heroLevel, boolean isMainRole, RoleCfg roleCfg, RobotEntryCfg robotCfg) {
		ArrayList<SkillInfo> skillInfoList = new ArrayList<SkillInfo>();
		Random r = new Random();
		checkAndAddSKillInfo(skillInfoList, r, heroLevel, roleCfg.getSkillId01(), isMainRole ? robotCfg.getFirstSkillLevel() : robotCfg.getHeroFirstSkillLevel());
		checkAndAddSKillInfo(skillInfoList, r, heroLevel, roleCfg.getSkillId02(), isMainRole ? robotCfg.getSecondSkillLevel() : robotCfg.getHeroSecondSkillLevel());
		checkAndAddSKillInfo(skillInfoList, r, heroLevel, roleCfg.getSkillId03(), isMainRole ? robotCfg.getThirdSkillLevel() : robotCfg.getHeroThirdSkillLevel());
		checkAndAddSKillInfo(skillInfoList, r, heroLevel, roleCfg.getSkillId04(), isMainRole ? robotCfg.getFourthSkillLevel() : robotCfg.getHeroFourthSkillLevel());
		checkAndAddSKillInfo(skillInfoList, r, heroLevel, roleCfg.getSkillId05(), isMainRole ? robotCfg.getFifthSkillLevel() : robotCfg.getHeroFifthSkillLevel());
		return skillInfoList;
	}

	/**
	 * 
	 * @param skillInfoList
	 * @param r
	 * @param heroLevel
	 * @param skillId
	 * @param skillLevelArray
	 */
	private static void checkAndAddSKillInfo(List<SkillInfo> skillInfoList, Random r, int heroLevel, String skillId, int[] skillLevelArray) {
		// 技能Id
		if (StringUtils.isEmpty(skillId)) {
			return;
		}

		// 技能等级
		if (skillLevelArray == null) {
			return;
		}

		int skillLevel = skillLevelArray[getRandomIndex(r, skillLevelArray.length)];
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
	 * 生成机器人的宝石
	 * 
	 * @param isMainRole
	 * @param angelRobotCfg
	 */
	public static ArrayList<String> getRobotGemList(boolean isMainRole, RobotEntryCfg angelRobotCfg) {
		Random r = new Random();
		// 宝石数量
		int[] gemCountArray = isMainRole ? angelRobotCfg.getGemCount() : angelRobotCfg.getHeroGemCount();
		int gemCount = gemCountArray[getRandomIndex(r, gemCountArray.length)];
		// // 宝石等级
		// int[] gemLevelArray = isMainRole ? angelRobotCfg.getGemLevel() : angelRobotCfg.getHeroGemLevel();
		int[] gemTypeArray = isMainRole ? angelRobotCfg.getGemType() : angelRobotCfg.getHeroGemType();
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

		ArrayList<String> canGemList = new ArrayList<String>(gemCount);
		for (int i = 0; i < gemCount; i++) {
			String gemId = String.valueOf(gemList.remove(getRandomIndex(r, gemList.size())));
			canGemList.add(gemId);
		}

		return canGemList;
	}

	/**
	 * 转换机器人存储的道术信息为对应的每个道术的等级信息
	 * 
	 * @param taoist
	 * @return
	 */
	public static Map<Integer, Integer> parseTaoist2Info(final int[] taoist) {
		Map<Integer, Integer> taoistLevelMap = new HashMap<Integer, Integer>();

		int len = taoist.length;
		for (int i = 0; i < len; i++) {
			int level = taoist[i];
			if (level <= 0) {
				continue;
			}

			int tag = i + 1;
			List<TaoistMagicCfg> list = TaoistMagicCfgHelper.getInstance().getTaoistCfgListByTag(tag);
			if (list == null || list.isEmpty()) {
				continue;
			}

			for (int j = 0, size = list.size(); j < size; j++) {
				taoistLevelMap.put(list.get(j).getKey(), level);
			}
		}

		return taoistLevelMap;
	}

	/**
	 * 转换配置中的神器信息到经验类道具
	 * 
	 * @param heroModelId
	 * @param fixEquip
	 * @return
	 */
	public static List<FixExpEquipDataItem> parseFixExpEquip2Info(int heroModelId, int[] fixEquip) {
		if (fixEquip == null || fixEquip.length < 3) {
			return Collections.emptyList();
		}

		RoleFixEquipCfg cfg = RoleFixEquipCfgDAO.getInstance().getConfig(String.valueOf(heroModelId));
		if (cfg == null) {
			return Collections.emptyList();
		}

		List<String> expCfgList = cfg.getExpCfgIdList();
		int size = expCfgList.size();

		List<FixExpEquipDataItem> fixExpEquipList = new ArrayList<FixExpEquipDataItem>(size);

		for (int i = 0; i < size; i++) {
			FixExpEquipDataItem expEquipDataItem = new FixExpEquipDataItem();
			if (fixEquip[0] == 0) {
				continue;
			}
			expEquipDataItem.setCfgId(expCfgList.get(i));
			expEquipDataItem.setLevel(fixEquip[0]);
			expEquipDataItem.setQuality(fixEquip[1]);
			expEquipDataItem.setStar(fixEquip[2]);
			fixExpEquipList.add(expEquipDataItem);
		}

		return fixExpEquipList;
	}

	/**
	 * 转换配置中的神器到普通类神器信息
	 * 
	 * @param heroModelId
	 * @param fixEquip
	 * @return
	 */
	public static List<FixNormEquipDataItem> parseFixNormEquip2Info(int heroModelId, int[] fixEquip) {
		if (fixEquip == null || fixEquip.length < 3) {
			return Collections.emptyList();
		}

		RoleFixEquipCfg cfg = RoleFixEquipCfgDAO.getInstance().getConfig(String.valueOf(heroModelId));
		if (cfg == null) {
			return Collections.emptyList();
		}

		List<String> normCfgList = cfg.getNormCfgIdList();
		int size = normCfgList.size();

		List<FixNormEquipDataItem> fixNormEquipList = new ArrayList<FixNormEquipDataItem>(size);

		for (int i = 0; i < size; i++) {
			FixNormEquipDataItem normEquipDataItem = new FixNormEquipDataItem();
			if (fixEquip[0] == 0) {
				continue;
			}
			normEquipDataItem.setCfgId(normCfgList.get(i));
			normEquipDataItem.setLevel(fixEquip[0]);
			normEquipDataItem.setQuality(fixEquip[1]);
			normEquipDataItem.setStar(fixEquip[2]);
			fixNormEquipList.add(normEquipDataItem);
		}

		return fixNormEquipList;
	}

	/**
	 * 生成详细的神器存储信息
	 * 
	 * @param heroModelId
	 * @param fixEquip
	 * @return
	 */
	public static List<HeroFixEquipInfo> parseFixInfo(int heroModelId, int[] fixEquip) {
		List<HeroFixEquipInfo> fixInfoList = new ArrayList<HeroFixEquipInfo>();
		List<HeroFixEquipInfo> fixExpList = FixEquipHelper.parseFixExpEquip2SimpleList(parseFixExpEquip2Info(heroModelId, fixEquip));
		if (!fixExpList.isEmpty()) {
			fixInfoList.addAll(fixExpList);
		}

		List<HeroFixEquipInfo> fixNormList = FixEquipHelper.parseFixNormEquip2SimpleList(parseFixNormEquip2Info(heroModelId, fixEquip));
		if (!fixNormList.isEmpty()) {
			fixInfoList.addAll(fixNormList);
		}

		return fixInfoList;
	}

	/**
	 * 转换机器人数据到羁绊数据
	 * 
	 * @param heroModelId
	 * @param fetters
	 * @return
	 */
	public static Map<Integer, SynConditionData> parseHeroFettersInfo(int heroModelId, final HeroFettersRobotInfo[] fetters) {
		FettersBaseCfgDAO cfgDAO = FettersBaseCfgDAO.getCfgDAO();
		FettersConditionCfgDAO conditionCfgDAO = FettersConditionCfgDAO.getCfgDAO();

		Map<Integer, SynConditionData> map = new HashMap<Integer, SynConditionData>();

		for (int i = 0, len = fetters.length; i < len; i++) {
			HeroFettersRobotInfo heroFettersRobotInfo = fetters[i];
			if (heroFettersRobotInfo == null) {
				continue;
			}

			String id = heroFettersRobotInfo.getId();
			int level = heroFettersRobotInfo.getLevel();

			int fettersId = Integer.parseInt(heroModelId + id);
			FettersBaseTemplate baseTmp = cfgDAO.getFettersBaseTemplateById(fettersId);
			if (baseTmp == null) {
				continue;
			}

			// 检查羁绊条件
			List<Integer> fettersConditionList = baseTmp.getFettersConditionList();
			if (fettersConditionList == null || fettersConditionList.isEmpty()) {
				continue;
			}

			SynConditionData synConditionData = new SynConditionData();
			List<Integer> l = new ArrayList<Integer>();

			for (int j = 0, size = fettersConditionList.size(); j < size; j++) {
				FettersConditionTemplate conditionTmp = conditionCfgDAO.getFettersConditionListByIdAndLevel(fettersConditionList.get(j), level);
				if (conditionTmp == null) {
					continue;
				}

				l.add(conditionTmp.getUniqueId());
			}

			synConditionData.setConditionList(l);

			map.put(fettersId, synConditionData);
		}

		return map;
	}

	/**
	 * 转换时装信息
	 * 
	 * @param fashionIdArr
	 * @return
	 */
	public static FashionInfo parseFashionInfo(int[] fashionIdArr) {
		FashionInfo fashionInfo = new FashionInfo();
		if (fashionIdArr == null || fashionIdArr.length != 3) {
			return fashionInfo;
		}

		fashionInfo.setSuit(fashionIdArr[0]);
		fashionInfo.setWing(fashionIdArr[1]);
		fashionInfo.setPet(fashionIdArr[2]);
		return fashionInfo;
	}
}