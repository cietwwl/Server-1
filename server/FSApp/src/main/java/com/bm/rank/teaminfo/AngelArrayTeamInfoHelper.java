package com.bm.rank.teaminfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bm.arena.ArenaRobotDataMgr;
import com.bm.rank.RankType;
import com.bm.rank.anglearray.AngleArrayComparable;
import com.bm.rank.teaminfo.AngelArrayTeamInfoCall.TeamInfoCallback;
import com.common.RefInt;
import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyMagic;
import com.playerdata.army.CurAttrData;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.HeroMgrIF;
import com.playerdata.readonly.PlayerIF;
import com.playerdata.team.EquipInfo;
import com.playerdata.team.FashionInfo;
import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroFixEquipInfo;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.SkillInfo;
import com.playerdata.team.TeamInfo;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwbase.dao.group.pojo.db.GroupSkillItem;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.skill.pojo.SkillHelper;
import com.rwproto.ArenaServiceProtos.ArenaEmbattleType;
import com.rwproto.BattleCommon.eBattlePositionType;

/*
 * @author HC
 * @date 2016年4月18日 下午3:26:45
 * @Description 
 */
public class AngelArrayTeamInfoHelper {

	/**
	 * 当角色登录的时候，刷新上线时间
	 * 
	 * @param p 角色信息
	 */
	public static void updateRankingEntry(Player p, TeamInfoCallback call) {
		Ranking<AngleArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		if (ranking == null) {
			return;
		}

		String userId = p.getUserId();
		RankingEntry<AngleArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(userId);
		if (rankingEntry == null) {
			return;
		}

		if (call == null) {
			return;
		}

		AngelArrayTeamInfoAttribute extendedAttribute = rankingEntry.getExtendedAttribute();
		call.call(p, extendedAttribute);
		ranking.subimitUpdatedTask(rankingEntry);
	}

	/**
	 * 当角色的等级改变了
	 * 
	 * @param p
	 */
	public static void updateRankingEntryWhenPlayerLevelChange(Player p) {
		Ranking<AngleArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		if (ranking == null) {
			return;
		}

		String userId = p.getUserId();
		RankingEntry<AngleArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(userId);
		if (rankingEntry == null) {
			return;
		}

		AngleArrayComparable comparable = rankingEntry.getComparable();
		int level = p.getLevel();
		if (level <= comparable.getLevel()) {
			return;
		}

		comparable = new AngleArrayComparable();
		comparable.setFighting(comparable.getFighting());
		comparable.setLevel(level);

		ranking.addOrUpdateRankingEntry(userId, comparable, rankingEntry.getExtendedAttribute());
	}

	/**
	 *
	 * @param p
	 * @param teamHeroList 竞技场的进攻阵容信息
	 * @return
	 */
	public static void checkAndUpdateTeamInfo(Player p, List<String> teamHeroList) {
		List<Integer> heroModelList = new ArrayList<Integer>();
		int fighting = getTeamInfoHeroModelListByTmpIdList(p, teamHeroList, heroModelList);
		checkAndUpdateTeamInfo(p, heroModelList, fighting);
	}

	/**
	 * 检查排行榜是否可以放入阵容信息
	 * 
	 * @param p
	 * @param heroModelList
	 * @param fighting
	 */
	public static void checkAndUpdateTeamInfo(Player p, List<Integer> heroModelList, int fighting) {
		Ranking<AngleArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		if (ranking == null) {
			return;
		}

		// 阵容中的战力
		int oldFighting = 0;
		String userId = p.getUserId();
		RankingEntry<AngleArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(userId);
		AngleArrayComparable comparable = null;
		if (rankingEntry != null) {
			comparable = rankingEntry.getComparable();
			oldFighting = comparable.getFighting();
		}

		if (fighting <= oldFighting) {
			return;
		}

		comparable = new AngleArrayComparable();
		comparable.setFighting(fighting);
		comparable.setLevel(p.getLevel());

		AngelArrayTeamInfoAttribute angelArrayTeamInfoAttribute = getAngelArrayTeamInfoAttribute(p, heroModelList);

		if (rankingEntry == null) {
			angelArrayTeamInfoAttribute.setTime(System.currentTimeMillis());
			ranking.addOrUpdateRankingEntry(userId, comparable, angelArrayTeamInfoAttribute);
		} else {
			rankingEntry.getExtendedAttribute().setTeamInfo(angelArrayTeamInfoAttribute.getTeamInfo());
			ranking.updateRankingEntry(rankingEntry, comparable);
		}
	}

	/**
	 * 从攻击阵容获取HeroModelIdList
	 * 
	 * @param p
	 * @param teamHeroList
	 * @param heroModelList
	 * @return
	 */
	public static int getTeamInfoHeroModelListByTmpIdList(PlayerIF p, List<String> teamHeroList, List<Integer> heroModelList) {
		if (p == null || teamHeroList == null || teamHeroList.isEmpty()) {
			return 0;
		}

		int fighting = p.getMainRoleHero().getFighting();

		int mainRoleModelId = p.getModelId();
		heroModelList.add(mainRoleModelId);

		HeroMgrIF heroMgr = p.getHeroMgr();
		int heroSize = teamHeroList.size();
		for (int i = 0; i < heroSize; i++) {
			int heroModelId;
			String heroTemplateId = teamHeroList.get(i);
			int indexOf = heroTemplateId.indexOf("_");
			if (indexOf == -1) {// 没有下划线
				heroModelId = Integer.parseInt(heroTemplateId);
			} else {
				heroModelId = Integer.parseInt(heroTemplateId.substring(0, indexOf));
			}

			if (heroModelId == mainRoleModelId) {
				continue;
			}

			HeroIF hero = heroMgr.getHeroByModerId(heroModelId);
			if (hero == null) {
				continue;
			}

			fighting += hero.getFighting();
			heroModelList.add(heroModelId);
		}

		return fighting;
	}

	/**
	 * 从防守阵容获取HeroModelIdList
	 * 
	 * @param p
	 * @param teamHeroList
	 * @param heroModelList
	 * @return
	 */
	public static int getTeamInfoHeroModelListByUUIDList(PlayerIF p, List<String> teamHeroList, List<Integer> heroModelList) {
		if (p == null || teamHeroList == null || teamHeroList.isEmpty()) {
			return 0;
		}

		int fighting = p.getMainRoleHero().getFighting();

		int mainRoleModelId = p.getModelId();
		heroModelList.add(mainRoleModelId);

		HeroMgrIF heroMgr = p.getHeroMgr();
		int heroSize = teamHeroList.size();
		for (int i = 0; i < heroSize; i++) {
			HeroIF hero = heroMgr.getHeroById(teamHeroList.get(i));
			if (hero == null) {
				continue;
			}

			int heroModelId = hero.getModelId();
			if (heroModelId == mainRoleModelId) {
				continue;
			}

			fighting += hero.getFighting();
			heroModelList.add(heroModelId);
		}

		return fighting;
	}

	/**
	 * 获取万仙阵阵容信息
	 * 
	 * @param p
	 * @param teamHeroList 佣兵的ModelId
	 * @return
	 */
	public static AngelArrayTeamInfoAttribute getAngelArrayTeamInfoAttribute(Player p, List<Integer> teamHeroList) {
		AngelArrayTeamInfoAttribute attr = new AngelArrayTeamInfoAttribute();
		attr.setUserId(p.getUserId());

		int modelId = p.getModelId();
		if (!teamHeroList.contains(modelId)) {
			teamHeroList.add(modelId);// 增加主角的模版Id
		}

		attr.setTeamInfo(parsePlayer2TeamInfo(p, teamHeroList));
		return attr;
	}

	/**
	 * 转换Player到TeamInfo
	 * 
	 * @param p
	 * @param teamHeroList 包含了主角在内的攻击英雄的ModelId
	 * @return
	 */
	public static TeamInfo parsePlayer2TeamInfo(Player p, List<Integer> teamHeroList) {
		TeamInfo teamInfo = new TeamInfo();
		// 主角的基础信息
		teamInfo.setCareer(p.getCareer());
		teamInfo.setVip(p.getVip());
		teamInfo.setName(p.getUserName());
		teamInfo.setHeadId(p.getHeadImage());
		teamInfo.setLevel(p.getLevel());
		teamInfo.setUuid(p.getUserId());

		// 帮派的基础信息
		changeGroupInfo(p, teamInfo);
		// 设置道术信息
		teamInfo.setTaoist(changeTaoistInfo(p));
		// 设置时装信息
		teamInfo.setFashion(changeFashionInfo(p));
		// 设置额外属性Id
		teamInfo.setExtraId(changeExtraAttrId(p));
		// 法宝信息
		teamInfo.setMagic(changeMagicInfo(p));

		// 英雄信息
		RefInt fighting = new RefInt();
		teamInfo.setHero(changeHeroInfo(p, teamHeroList, fighting));
		teamInfo.setTeamFighting(fighting.value);
		return teamInfo;
	}

	/**
	 * 道术信息
	 * 
	 * @param p
	 * @return
	 */
	private static Map<Integer, Integer> changeTaoistInfo(Player p) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		if (!p.isRobot()) {
			Iterable<Entry<Integer, Integer>> allTaoist = p.getTaoistMgr().getAllTaoist();
			for (Entry<Integer, Integer> info : allTaoist) {
				int level = info.getValue();
				if (level <= 0) {
					continue;
				}

				map.put(info.getKey(), info.getValue());
			}

			return map;
		}

		return ArenaRobotDataMgr.getMgr().getRobotTaoistMap(p.getUserId());
	}

	/**
	 * 设置时装信息
	 * 
	 * @param p
	 * @return
	 */
	private static FashionInfo changeFashionInfo(Player p) {
		FashionInfo fashionInfo = new FashionInfo();
		if (!p.isRobot()) {
			FashionUsedIF fashionUsed = p.getFashionMgr().getFashionUsed();
			fashionInfo.setSuit(fashionUsed.getSuitId());
			fashionInfo.setWing(fashionUsed.getWingId());
			fashionInfo.setPet(fashionUsed.getPetId());
			fashionInfo.setCount(p.getFashionMgr().getValidCount());
			return fashionInfo;
		}

		int[] fashionIdArr = ArenaRobotDataMgr.getMgr().getFashionIdArr(p.getUserId());
		if (fashionIdArr == null || fashionIdArr.length != 3) {
			return fashionInfo;
		}

		fashionInfo.setSuit(fashionIdArr[0]);
		fashionInfo.setWing(fashionIdArr[1]);
		fashionInfo.setPet(fashionIdArr[2]);
		return null;
	}

	/**
	 * 获取额外的属性Id
	 * 
	 * @param p
	 * @return
	 */
	private static int changeExtraAttrId(Player p) {
		if (!p.isRobot()) {
			return -1;
		}

		return ArenaRobotDataMgr.getMgr().getExtraAttrId(p.getUserId());
	}

	/**
	 * 改变帮派信息
	 * 
	 * @param p
	 * @param teamInfo
	 */
	private static void changeGroupInfo(Player p, TeamInfo teamInfo) {
		UserGroupAttributeDataIF userGroupAttributeData = p.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		if (userGroupAttributeData != null) {
			teamInfo.setGroupName(userGroupAttributeData.getGroupName());
			// 帮派技能
			List<GroupSkillItem> skillItemList = userGroupAttributeData.getSkillItemList();
			if (skillItemList != null && !skillItemList.isEmpty()) {
				int size = skillItemList.size();
				Map<Integer, Integer> skillMap = new HashMap<Integer, Integer>(size);
				for (int i = 0; i < size; i++) {
					GroupSkillItem groupSkillItem = skillItemList.get(i);
					if (groupSkillItem == null || groupSkillItem.getLevel() <= 0) {
						continue;
					}

					skillMap.put(Integer.valueOf(groupSkillItem.getId()), groupSkillItem.getLevel());
				}

				teamInfo.setGs(skillMap);
			}
		}
	}

	/**
	 * 设置英雄的法宝信息
	 * 
	 * @param p
	 * @return
	 */
	private static ArmyMagic changeMagicInfo(Player p) {
		ItemData magic = p.getMagic();
		ArmyMagic magicInfo = new ArmyMagic();
		if (magic != null) {
			magicInfo.setModelId(magic.getModelId());
			magicInfo.setLevel(magic.getMagicLevel());
		}

		return magicInfo;
	}

	/**
	 * 设置英雄信息
	 * 
	 * @param p
	 * @param teamHeroList
	 * @param fighting
	 * @return
	 */
	private static List<HeroInfo> changeHeroInfo(Player p, List<Integer> teamHeroList, RefInt fighting) {
		// 获取竞技场的阵容
		EmbattlePositionInfo posInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(p.getUserId(), eBattlePositionType.ArenaPos_VALUE,
			String.valueOf(ArenaEmbattleType.ARENA_ATK_VALUE));// 竞技场的攻击阵容

		// 英雄的阵容
		int heroSize = teamHeroList.size();
		HeroMgr heroMgr = p.getHeroMgr();
		// 获取阵容信息
		int mainRoleIndex = -1;
		List<HeroInfo> heroList = new ArrayList<HeroInfo>(heroSize);
		for (int i = 0; i < heroSize; i++) {
			int heroModelId = teamHeroList.get(i);
			Hero hero = heroMgr.getHeroByModerId(heroModelId);
			if (hero == null) {
				continue;
			}

			HeroInfo heroInfo = new HeroInfo();
			// 基础属性
			heroInfo.setBaseInfo(changeHeroBaseInfo(hero));
			// 装备
			List<EquipInfo> equipList = changeHeroEquipList(hero);
			if (equipList != null) {
				heroInfo.setEquip(equipList);
			}
			// 宝石
			heroInfo.setGem(hero.getInlayMgr().getInlayGemList());
			// 技能
			List<SkillInfo> skillList = changeHeroSkillList(hero);
			if (skillList != null) {
				heroInfo.setSkill(skillList);
			}

			// 羁绊
			Map<Integer, SynConditionData> fetters = changeHeroFetters(p, hero);
			if (fetters != null) {
				heroInfo.setFetters(fetters);
			}

			// 神器
			List<HeroFixEquipInfo> fixEquipList = changeHeroFixEquip(p, hero);
			if (fixEquipList != null) {
				heroInfo.setFixEquip(fixEquipList);
			}

			// 站位
			int heroPos = 0;
			if (hero.isMainRole()) {
				mainRoleIndex = i;
			} else {
				heroPos = posInfo == null ? (mainRoleIndex == -1 ? i + 1 : i) : posInfo.getHeroPos(hero.getTemplateId());
			}

			heroInfo.getBaseInfo().setPos(heroPos);

			heroList.add(heroInfo);
			fighting.value += hero.getFighting();
		}

		return heroList;
	}

	/**
	 * 设置英雄的基础信息
	 * 
	 * @param hero
	 * @return
	 */
	private static HeroBaseInfo changeHeroBaseInfo(Hero hero) {
		HeroBaseInfo heroBaseInfo = new HeroBaseInfo();
		heroBaseInfo.setLevel(hero.getLevel());
		heroBaseInfo.setStar(hero.getStarLevel());
		heroBaseInfo.setQuality(hero.getQualityId());
		heroBaseInfo.setTmpId(hero.getTemplateId());
		return heroBaseInfo;
	}

	/**
	 * 设置英雄的装备信息
	 * 
	 * @param hero
	 * @return
	 */
	private static List<EquipInfo> changeHeroEquipList(Hero hero) {
		List<EquipInfo> equipInfoList = null;

		List<EquipItem> equipList = hero.getEquipMgr().getEquipList();
		if (equipList != null && !equipList.isEmpty()) {
			int size = equipList.size();

			equipInfoList = new ArrayList<EquipInfo>(size);
			for (int j = 0; j < size; j++) {
				EquipItem equipItem = equipList.get(j);
				if (equipItem == null) {
					continue;
				}

				EquipInfo equipInfo = new EquipInfo();
				equipInfo.settId(String.valueOf(equipItem.getModelId()));
				equipInfo.seteLevel(equipItem.getLevel());

				equipInfoList.add(equipInfo);
			}
		}

		return equipInfoList;
	}

	/**
	 * 设置英雄技能
	 * 
	 * @param hero
	 * @return
	 */
	private static List<SkillInfo> changeHeroSkillList(Hero hero) {
		List<SkillInfo> skillInfoList = null;

		List<Skill> skillList = hero.getSkillMgr().getSkillList();
		if (skillList != null && !skillList.isEmpty()) {
			int size = skillList.size();

			skillInfoList = new ArrayList<SkillInfo>(size);
			for (int j = 0; j < size; j++) {
				Skill skill = skillList.get(j);
				if (skill == null) {
					continue;
				}

				SkillInfo skillInfo = new SkillInfo();
				skillInfo.setSkillId(skill.getSkillId());
				skillInfo.setSkillLevel(skill.getLevel());

				skillInfoList.add(skillInfo);
			}
		}

		return skillInfoList;
	}

	/**
	 * 获取羁绊信息
	 * 
	 * @param player
	 * @param hero
	 * @return
	 */
	private static Map<Integer, SynConditionData> changeHeroFetters(Player player, Hero hero) {
		if (!player.isRobot()) {
			SynFettersData fettersData = player.getHeroFettersByModelId(hero.getModelId());
			if (fettersData == null) {
				return null;
			}

			return fettersData.getOpenList();
		}

		return ArenaRobotDataMgr.getMgr().getHeroFettersInfo(player.getUserId(), hero.getModelId());
	}

	/**
	 * 设置神器信息
	 * 
	 * @param player
	 * @param hero
	 * @return
	 */
	private static List<HeroFixEquipInfo> changeHeroFixEquip(Player player, Hero hero) {
		List<HeroFixEquipInfo> fixInfoList = new ArrayList<HeroFixEquipInfo>();

		if (!player.isRobot()) {
			List<HeroFixEquipInfo> fixExpList = hero.getFixExpEquipMgr().getHeroFixSimpleInfo(hero.getUUId());
			if (!fixExpList.isEmpty()) {
				fixInfoList.addAll(fixExpList);
			}

			List<HeroFixEquipInfo> fixNormList = hero.getFixNormEquipMgr().getHeroFixSimpleInfo(hero.getUUId());
			if (!fixNormList.isEmpty()) {
				fixInfoList.addAll(fixNormList);
			}

			return fixInfoList;
		}

		String userId = player.getUserId();
		int heroModelId = hero.getModelId();

		ArenaRobotDataMgr mgr = ArenaRobotDataMgr.getMgr();
		List<HeroFixEquipInfo> fixExpList = FixEquipHelper.parseFixExpEquip2SimpleList(mgr.getFixExpEquipList(userId, heroModelId));
		if (!fixExpList.isEmpty()) {
			fixInfoList.addAll(fixExpList);
		}

		List<HeroFixEquipInfo> fixNormList = FixEquipHelper.parseFixNormEquip2SimpleList(mgr.getFixNormEquipList(userId, heroModelId));
		if (!fixNormList.isEmpty()) {
			fixInfoList.addAll(fixNormList);
		}

		return fixInfoList;
	}

	/**
	 * <pre>
	 * 把TeamInfo转换成ArmyInfo
	 * 限于条件，ArmyInfo中没有设置帮派名字，头像，名字等信息
	 * </pre>
	 * 
	 * @param teamInfo
	 * @return
	 */
	public static ArmyInfo parseTeamInfo2ArmyInfo(TeamInfo teamInfo) {
		ArmyInfo armyInfo = new ArmyInfo();
		if (teamInfo == null) {
			return armyInfo;
		}

		ArmyMagic magic = teamInfo.getMagic();
		armyInfo.setArmyMagic(magic);

		List<HeroInfo> heroList = teamInfo.getHero();
		int size = heroList.size();

		// 英雄属性
		boolean nonHeroPos = false;
		List<ArmyHero> armyHeroList = new ArrayList<ArmyHero>(size);
		for (int i = 0; i < size; i++) {
			ArmyHero armyHero = parseHeroInfo2ArmyHero(heroList.get(i), teamInfo);

			if (armyHero.isPlayer()) {
				armyInfo.setPlayer(armyHero);
			} else {
				armyHeroList.add(armyHero);
				if (!nonHeroPos) {
					nonHeroPos = armyHero.getPosition() == 0;
				}
			}
		}

		// 有英雄没有站位，全部按照1~4排位
		for (int i = 0, aSize = armyHeroList.size(); i < aSize; i++) {
			armyHeroList.get(i).setPosition(i + 1);
		}

		armyInfo.setHeroList(armyHeroList);
		armyInfo.setPlayerName(teamInfo.getName());
		armyInfo.setPlayerHeadImage(teamInfo.getHeadId());
		armyInfo.setGuildName(teamInfo.getGroupName());

		return armyInfo;
	}

	/**
	 * 
	 * @param heroInfo
	 * @param magicLevel
	 * @return
	 */
	private static ArmyHero parseHeroInfo2ArmyHero(HeroInfo heroInfo, TeamInfo teamInfo) {
		ArmyHero armyHero = new ArmyHero();

		RoleCfgDAO cfgDAO = RoleCfgDAO.getInstance();
		// 基础
		HeroBaseInfo baseInfo = heroInfo.getBaseInfo();
		String tmpId = baseInfo.getTmpId();
		RoleCfg roleCfg = cfgDAO.getCfgById(tmpId);

		RoleBaseInfo roleBaseInfo = new RoleBaseInfo();
		roleBaseInfo.setId(tmpId);
		roleBaseInfo.setLevel(baseInfo.getLevel());
		roleBaseInfo.setQualityId(baseInfo.getQuality());
		roleBaseInfo.setStarLevel(baseInfo.getStar());
		roleBaseInfo.setModeId(roleCfg.getModelId());
		roleBaseInfo.setTemplateId(tmpId);
		roleBaseInfo.setCareerType(roleCfg.getCareerType());
		armyHero.setRoleBaseInfo(roleBaseInfo);
		// 技能
		int skillLevel = 0;
		List<SkillInfo> skillInfoList = heroInfo.getSkill();
		int size = skillInfoList.size();

		SkillCfgDAO skillCfgDAO = SkillCfgDAO.getInstance();

		List<Skill> skillList = SkillHelper.initSkill(roleCfg, baseInfo.getQuality(), baseInfo.getLevel());

		int skillSize = skillList.size();

		for (int i = 0; i < size; i++) {
			SkillInfo skillInfo = skillInfoList.get(i);
			if (skillInfo == null) {
				continue;
			}

			String skillId = skillInfo.getSkillId();
			SkillCfg skillCfg = skillCfgDAO.getCfg(skillId);
			if (skillCfg == null) {
				continue;
			}

			int sLevel = skillInfo.getSkillLevel();

			for (int j = 0; j < skillSize; j++) {
				Skill skill = skillList.get(i);
				if (skill == null) {
					continue;
				}

				String skillModelId = skillId.split("_")[0];
				if (!skill.getSkillId().startsWith(skillModelId)) {
					continue;
				}

				skill.setLevel(sLevel);
				skill.setSkillId(skillModelId + "_" + sLevel);
			}

			skillLevel += sLevel;
		}

		armyHero.setSkillList(skillList);
		// 其他属性
		AttrData heroAttrData = AttributeBM.getRobotAttrData(tmpId, heroInfo, teamInfo);
		armyHero.setAttrData(heroAttrData);
		// 当前血量
		CurAttrData curAttrData = new CurAttrData();
		curAttrData.setCurLife(heroAttrData.getLife());
		armyHero.setCurAttrData(curAttrData);
		// 设置是否是主角
		boolean isPlayer = roleCfg.getRoleType() == 1;
		armyHero.setPlayer(isPlayer);
		// 计算战斗力
		armyHero.setFighting(FightingCalculator.calFighting(tmpId, skillLevel, isPlayer ? teamInfo.getMagic().getLevel() : 0, isPlayer ? String.valueOf(teamInfo.getMagic().getModelId()) : "",
			heroAttrData));
		// 设置站位
		armyHero.setPosition(heroInfo.getBaseInfo().getPos());

		return armyHero;
	}
}