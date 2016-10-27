package com.bm.rank.teaminfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.bm.arena.ArenaRobotDataMgr;
import com.bm.rank.RankType;
import com.bm.rank.angelarray.AngelArrayComparable;
import com.bm.rank.teaminfo.AngelArrayTeamInfoCall.TeamInfoCallback;
import com.common.BeanOperationHelper;
import com.common.RefInt;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.SpriteAttachMgr;
import com.playerdata.eRoleType;
import com.playerdata.army.ArmyFashion;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyMagic;
import com.playerdata.army.CurAttrData;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.fightinggrowth.calc.param.FashionFightingParam;
import com.playerdata.fightinggrowth.calc.param.FettersFightingParam;
import com.playerdata.fightinggrowth.calc.param.FixEquipFightingParam;
import com.playerdata.fightinggrowth.calc.param.HeroBaseFightingParam;
import com.playerdata.fightinggrowth.calc.param.HeroBaseFightingParam.Builder;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.hero.core.RoleBaseInfo;
import com.playerdata.readonly.ItemDataIF;
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
import com.rwbase.common.attribute.param.EquipParam.EquipBuilder;
import com.rwbase.common.attribute.param.GemParam.GemBuilder;
import com.rwbase.common.attribute.param.GroupSkillParam.GroupSkillBuilder;
import com.rwbase.common.attribute.param.MagicParam.MagicBuilder;
import com.rwbase.common.attribute.param.SkillParam.SkillBuilder;
import com.rwbase.common.attribute.param.TaoistParam.TaoistBuilder;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.equipment.EquipItemIF;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.fetters.pojo.SynConditionData;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwbase.dao.group.pojo.db.GroupSkillItem;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.skill.pojo.SkillHelper;
import com.rwbase.dao.skill.pojo.SkillIF;
import com.rwbase.dao.spriteattach.SpriteAttachItem;
import com.rwbase.dao.skill.pojo.SkillItem;
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
		Ranking<AngelArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		if (ranking == null) {
			return;
		}

		String userId = p.getUserId();
		RankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(userId);
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
		Ranking<AngelArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		if (ranking == null) {
			return;
		}

		String userId = p.getUserId();
		RankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(userId);
		if (rankingEntry == null) {
			return;
		}

		AngelArrayComparable comparable = rankingEntry.getComparable();
		int level = p.getLevel();
		if (level <= comparable.getLevel()) {
			return;
		}

		comparable = new AngelArrayComparable();
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
		int fighting = getTeamInfoHeroModelListById(p, teamHeroList, heroModelList);
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
		Ranking<AngelArrayComparable, AngelArrayTeamInfoAttribute> ranking = RankingFactory.getRanking(RankType.ANGEL_TEAM_INFO_RANK);
		if (ranking == null) {
			return;
		}

		// 阵容中的战力
		int oldFighting = 0;
		String userId = p.getUserId();
		RankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute> rankingEntry = ranking.getRankingEntry(userId);
		AngelArrayComparable comparable = null;
		if (rankingEntry != null) {
			comparable = rankingEntry.getComparable();
			oldFighting = comparable.getFighting();
		}

		if (fighting <= oldFighting) {
			return;
		}

		comparable = new AngelArrayComparable();
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
	// public static int getTeamInfoHeroModelListByTmpIdList(PlayerIF p,
	// List<String> teamHeroList, List<Integer> heroModelList) {
	// if (p == null || teamHeroList == null || teamHeroList.isEmpty()) {
	// return 0;
	// }
	//
	// int fighting = p.getMainRoleHero().getFighting();
	//
	// int mainRoleModelId = p.getModelId();
	// heroModelList.add(mainRoleModelId);
	//
	// HeroMgrIF heroMgr = p.getHeroMgr();
	// int heroSize = teamHeroList.size();
	// for (int i = 0; i < heroSize; i++) {
	// int heroModelId;
	// String heroTemplateId = teamHeroList.get(i);
	// int indexOf = heroTemplateId.indexOf("_");
	// if (indexOf == -1) {// 没有下划线
	// heroModelId = Integer.parseInt(heroTemplateId);
	// } else {
	// heroModelId = Integer.parseInt(heroTemplateId.substring(0, indexOf));
	// }
	//
	// if (heroModelId == mainRoleModelId) {
	// continue;
	// }
	//
	// HeroIF hero = heroMgr.getHeroByModerId(heroModelId);
	// if (hero == null) {
	// continue;
	// }
	//
	// fighting += hero.getFighting();
	// heroModelList.add(heroModelId);
	// }
	//
	// return fighting;
	// }

	public static int getTeamInfoHeroModelListById(PlayerIF p, List<String> teamHeroList, List<Integer> heroModelList) {
		if (p == null || teamHeroList == null || teamHeroList.isEmpty()) {
			return 0;
		}

		int fighting = p.getMainRoleHero().getFighting();

		int mainRoleModelId = p.getModelId();
		heroModelList.add(mainRoleModelId);

		// HeroMgrIF heroMgr = p.getHeroMgr();
		HeroMgr heroMgr = p.getHeroMgr();
		int heroSize = teamHeroList.size();
		for (int i = 0; i < heroSize; i++) {
			String uuid = teamHeroList.get(i);
			// HeroIF hero = heroMgr.getHeroById(uuid);
			Hero hero = heroMgr.getHeroById(p, uuid);
			if (hero == null) {
				GameLog.error("AngelArrayTeamInfoHelper", p.getUserName(), "get hero by uuid fail:" + uuid);
				continue;
			}
			fighting += hero.getFighting();

			int modelId = hero.getModeId();
			if (!heroModelList.contains(modelId)) {
				heroModelList.add(modelId);
			}
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

		// HeroMgrIF heroMgr = p.getHeroMgr();
		HeroMgr heroMgr = p.getHeroMgr();
		int heroSize = teamHeroList.size();
		for (int i = 0; i < heroSize; i++) {
			// HeroIF hero = heroMgr.getHeroById(teamHeroList.get(i));
			Hero hero = heroMgr.getHeroById(p, teamHeroList.get(i));
			if (hero == null) {
				continue;
			}

			int heroModelId = hero.getModeId();
			if (heroModelId == mainRoleModelId) {
				continue;
			}

			fighting += hero.getFighting();
			if (!heroModelList.contains(heroModelId)) {
				heroModelList.add(heroModelId);
			}
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
	public static TeamInfo parsePlayer2TeamInfo(PlayerIF p, List<Integer> teamHeroList) {
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
	private static Map<Integer, Integer> changeTaoistInfo(PlayerIF p) {
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
	private static FashionInfo changeFashionInfo(PlayerIF p) {
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
		return fashionInfo;
	}

	/**
	 * 获取额外的属性Id
	 * 
	 * @param p
	 * @return
	 */
	private static int changeExtraAttrId(PlayerIF p) {
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
	private static void changeGroupInfo(PlayerIF p, TeamInfo teamInfo) {
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
	private static ArmyMagic changeMagicInfo(PlayerIF p) {
		ItemDataIF magic = p.getMagic();
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
	private static List<HeroInfo> changeHeroInfo(PlayerIF p, List<Integer> teamHeroList, RefInt fighting) {
		// 获取竞技场的阵容
		EmbattlePositionInfo posInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(p.getUserId(), eBattlePositionType.ArenaPos_VALUE, String.valueOf(ArenaEmbattleType.ARENA_ATK_VALUE));// 竞技场的攻击阵容

		// 英雄的阵容
		int heroSize = teamHeroList.size();
		HeroMgr heroMgr = p.getHeroMgr();
		// 获取阵容信息
		int mainRoleIndex = -1;
		List<HeroInfo> heroList = new ArrayList<HeroInfo>(heroSize);
		for (int i = 0; i < heroSize; i++) {
			int heroModelId = teamHeroList.get(i);
			// Hero hero = heroMgr.getHeroByModerId(heroModelId);
			Hero hero = heroMgr.getHeroByModerId(p, heroModelId);
			if (hero == null) {
				continue;
			}

			HeroInfo heroInfo = buildHeroInfo(p, hero);

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
	 * 构造英雄
	 * 
	 * @param p
	 * @param hero
	 * @return
	 */
	private static HeroInfo buildHeroInfo(PlayerIF p, Hero hero) {
		HeroInfo heroInfo = new HeroInfo();
		// 基础属性
		heroInfo.setBaseInfo(changeHeroBaseInfo(hero));
		// 装备
		List<EquipInfo> equipList = changeHeroEquipList(hero);
		if (equipList != null) {
			heroInfo.setEquip(equipList);
		}
		// 宝石
		heroInfo.setGem(hero.getInlayMgr().getInlayGemList(p, hero.getId()));
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
		
		//附灵
		List<SpriteAttachItem> spriteAttachList = changeHeroSpriteAttach(p, hero);
		if(spriteAttachList != null){
			heroInfo.setSpriteAttach(spriteAttachList);
		}

		return heroInfo;
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

		List<EquipItem> equipList = hero.getEquipMgr().getEquipList(hero.getUUId());
		if (equipList != null && !equipList.isEmpty()) {
			int size = equipList.size();

			equipInfoList = new ArrayList<EquipInfo>(size);
			for (int j = 0; j < size; j++) {
				EquipItemIF equipItem = equipList.get(j);
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

		List<SkillItem> skillList = hero.getSkillMgr().getSkillList(hero.getUUId());
		if (skillList != null && !skillList.isEmpty()) {
			int size = skillList.size();

			skillInfoList = new ArrayList<SkillInfo>(size);
			for (int j = 0; j < size; j++) {
				SkillIF skill = skillList.get(j);
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
	private static Map<Integer, SynConditionData> changeHeroFetters(PlayerIF player, Hero hero) {
		if (!player.isRobot()) {
			SynFettersData fettersData = player.getHeroFettersByModelId(hero.getModeId());
			if (fettersData == null) {
				return null;
			}

			return fettersData.getOpenList();
		}

		return ArenaRobotDataMgr.getMgr().getHeroFettersInfo(player.getUserId(), hero.getModeId());
	}

	/**
	 * 设置神器信息
	 * 
	 * @param player
	 * @param hero
	 * @return
	 */
	private static List<HeroFixEquipInfo> changeHeroFixEquip(PlayerIF player, Hero hero) {
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
		int heroModelId = hero.getModeId();

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
	 * 设置附灵信息
	 * @param player
	 * @param hero
	 * @return
	 */
	private static List<SpriteAttachItem> changeHeroSpriteAttach(PlayerIF player, Hero hero){
		List<SpriteAttachItem> spriteAttachList = new ArrayList<SpriteAttachItem>();
		if (!player.isRobot()) {
			List<SpriteAttachItem> spriteAttachItemList = SpriteAttachMgr.getInstance().getSpriteAttachHolder().getSpriteAttachItemList(hero.getUUId());
			if (!spriteAttachItemList.isEmpty()) {
				spriteAttachList.addAll(spriteAttachItemList);
			}
		}
		return spriteAttachList;
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
		boolean hasMainRole = false;
		boolean nonHeroPos = false;
		List<ArmyHero> armyHeroList = new ArrayList<ArmyHero>(size);
		for (int i = 0; i < size; i++) {
			ArmyHero armyHero = parseHeroInfo2ArmyHero(heroList.get(i), teamInfo);

			if (armyHero.isPlayer()) {
				armyInfo.setPlayer(armyHero);
				hasMainRole = true;
			} else {
				armyHeroList.add(armyHero);
				if (!nonHeroPos) {
					int position = armyHero.getPosition();
					nonHeroPos = position == 0 || position >= 5;
				}
			}
		}

		// 有英雄没有站位，全部按照1~4排位
		if (nonHeroPos) {
			for (int i = 0, aSize = armyHeroList.size(); i < aSize; i++) {
				armyHeroList.get(i).setPosition(hasMainRole ? (i + 1) : i);
			}
		}

		armyInfo.setHeroList(armyHeroList);
		armyInfo.setPlayerName(teamInfo.getName());
		armyInfo.setPlayerHeadImage(teamInfo.getHeadId());
		armyInfo.setGuildName(teamInfo.getGroupName());

		// 把时装也加上
		FashionInfo fashion = teamInfo.getFashion();
		ArmyHero player = armyInfo.getPlayer();
		if (player != null) {
			if (fashion != null) {
				if (fashion.getSuit() > 0 || fashion.getWing() > 0 || fashion.getPet() > 0) {
					ArmyFashion armyFashion = new ArmyFashion();
					armyFashion.setCareer(teamInfo.getCareer());
					RoleCfg roleCfg = RoleCfgDAO.getInstance().getCfgById(player.getRoleBaseInfo().getTemplateId());

					if (roleCfg != null) {
						armyFashion.setGender(roleCfg.getSex());
					}

					armyFashion.setSuitId(fashion.getSuit());
					armyFashion.setWingId(fashion.getWing());
					armyFashion.setPetId(fashion.getPet());

					armyInfo.setArmyFashion(armyFashion);
				}
			}
		}

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

		// 设置是否是主角
		boolean isPlayer = roleCfg.getRoleType() == 1;
		armyHero.setPlayer(isPlayer);

		RoleBaseInfo roleBaseInfo = new RoleBaseInfo();
		if (isPlayer) {
			roleBaseInfo.setId(teamInfo.getUuid());
		} else {
			roleBaseInfo.setId(tmpId);
		}
		roleBaseInfo.setLevel(baseInfo.getLevel());
		roleBaseInfo.setQualityId(baseInfo.getQuality());
		roleBaseInfo.setStarLevel(baseInfo.getStar());
		roleBaseInfo.setModeId(roleCfg.getModelId());
		roleBaseInfo.setTemplateId(tmpId);
		roleBaseInfo.setCareerType(roleCfg.getCareerType());
		armyHero.setRoleBaseInfo(roleBaseInfo);
		// 技能
		// int skillLevel = 0;
		List<SkillInfo> skillInfoList = heroInfo.getSkill();
		int size = skillInfoList.size();

		SkillCfgDAO skillCfgDAO = SkillCfgDAO.getInstance();

		List<SkillItem> skillList = SkillHelper.initSkill(roleCfg, baseInfo.getQuality(), baseInfo.getLevel());

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
				SkillItem skill = skillList.get(i);
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

			// skillLevel += sLevel;
		}

		armyHero.setSkillList(skillList);
		// 其他属性
		AttrData heroAttrData = AttributeBM.getRobotAttrData(tmpId, heroInfo, teamInfo);
		armyHero.setAttrData(heroAttrData);
		// 当前血量
		CurAttrData curAttrData = new CurAttrData();
		curAttrData.setCurLife(heroAttrData.getLife());
		curAttrData.setMaxLife(heroAttrData.getLife());
		curAttrData.setCurEnergy(0);
		curAttrData.setMaxEnergy(1000);
		armyHero.setCurAttrData(curAttrData);

		// 计算战斗力
		// armyHero.setFighting(FightingCalculator.calFighting(tmpId, skillLevel, isPlayer ? teamInfo.getMagic().getLevel() : 0, isPlayer ? String.valueOf(teamInfo.getMagic().getModelId()) : "",
		// heroAttrData));
		armyHero.setFighting(calcRobotFighting(heroInfo, teamInfo));
		// 设置站位
		armyHero.setPosition(heroInfo.getBaseInfo().getPos());

		return armyHero;
	}

	/**
	 * 获取机器人战斗力
	 * 
	 * @param heroInfo
	 * @param teamInfo
	 * @return
	 */
	public static int calcRobotFighting(HeroInfo heroInfo, TeamInfo teamInfo) {
		String tmpId = heroInfo.getBaseInfo().getTmpId();
		RoleCfg roleCfg = RoleCfgDAO.getInstance().getCfgById(tmpId);
		if (roleCfg == null) {
			return 0;
		}

		boolean isMainRole = roleCfg.getRoleType() == eRoleType.Player.ordinal();// 是否是主角

		int fighting = 0;

		// 基础战力
		HeroBaseFightingParam.Builder heroBaseBuilder = new Builder();
		heroBaseBuilder.setHeroTmpId(tmpId);
		AttrData robotBaseAttrData = AttributeBM.getRobotBaseAttrData(tmpId, heroInfo, teamInfo);
		heroBaseBuilder.setBaseData(robotBaseAttrData);
		int base = FightingCalcComponentType.BASE.calc.calc(heroBaseBuilder.build());
		fighting += base;

		// 装备战力
		EquipBuilder eb = new EquipBuilder();
		eb.setHeroId(tmpId);
		eb.setEquipList(heroInfo.getEquip());
		int equip = FightingCalcComponentType.EQUIP.calc.calc(eb.build());
		fighting += equip;

		// 羁绊战力
		FettersFightingParam.Builder ffb = new com.playerdata.fightinggrowth.calc.param.FettersFightingParam.Builder();
		ffb.setHeroFetters(heroInfo.getFetters());
		int fetters = FightingCalcComponentType.FETTERS.calc.calc(ffb.build());
		fighting += fetters;

		// 神器战力
		FixEquipFightingParam.Builder feb = new com.playerdata.fightinggrowth.calc.param.FixEquipFightingParam.Builder();
		feb.setFixEquips(heroInfo.getFixEquip());
		int fixEquip = FightingCalcComponentType.FIX_EQUIP.calc.calc(feb.build());
		fighting += fixEquip;

		// 宝石战力
		GemBuilder gb = new GemBuilder();
		gb.setHeroId(tmpId);
		gb.setGemList(heroInfo.getGem());
		int gem = FightingCalcComponentType.GEM.calc.calc(gb.build());
		fighting += gem;

		// 帮派技能战力
		GroupSkillBuilder gsb = new GroupSkillBuilder();
		gsb.setHeroId(tmpId);
		gsb.setGroupSkillMap(teamInfo.getGs());
		int gs = FightingCalcComponentType.GROUP_SKILL.calc.calc(gsb.build());
		fighting += gs;

		// 技能战力
		SkillBuilder sb = new SkillBuilder();
		sb.setHeroId(tmpId);
		sb.setSkillList(heroInfo.getSkill());
		int skill = FightingCalcComponentType.SKILL.calc.calc(sb.build());
		fighting += skill;

		// 道术战力
		TaoistBuilder tb = new TaoistBuilder();
		tb.setHeroId(tmpId);
		tb.setTaoistMap(teamInfo.getTaoist());
		int taoist = FightingCalcComponentType.TAOIST.calc.calc(tb.build());
		fighting += taoist;

		// =======================================================主角才有的战力
		int mf = 0;
		int fashionF = 0;

		if (isMainRole) {
			// 法宝战力
			ArmyMagic magic = teamInfo.getMagic();
			if (magic != null) {
				MagicBuilder mb = new MagicBuilder();
				mb.setHeroId(tmpId);
				mb.setMagicId(String.valueOf(magic.getModelId()));
				mb.setMagicLevel(magic.getLevel());
				mb.setMagicAptitude(magic.getAptitude());
				mf = FightingCalcComponentType.MAGIC.calc.calc(mb.build());
				fighting += mf;
			}

			// 时装战力
			FashionInfo fashion = teamInfo.getFashion();
			if (fashion != null) {
				int suitCount = fashion.getSuit() <= 0 ? 0 : 1;
				int wingCount = fashion.getWing() <= 0 ? 0 : 1;
				int petCount = fashion.getPet() <= 0 ? 0 : 1;

				FashionFightingParam.Builder fashionB = new com.playerdata.fightinggrowth.calc.param.FashionFightingParam.Builder();
				fashionB.setSuitCount(suitCount);
				fashionB.setWingCount(wingCount);
				fashionB.setPetCount(petCount);
				fashionF = FightingCalcComponentType.FASHION.calc.calc(fashionB.build());
				fighting += fashionF;
			}
		}

		// TODO HC 等牙玺调试完成这些代码就全部去掉
		StringBuilder printSB = new StringBuilder();
		printSB.append(teamInfo.getName()).append("：[").append(tmpId).append("]-->");
		printSB.append("属性：").append(BeanOperationHelper.getPositiveValueDiscription(robotBaseAttrData)).append("\n");
		printSB.append("\t基础战力：").append(base).append("--");
		printSB.append("装备战力：").append(equip).append("--");
		printSB.append("羁绊战力：").append(equip).append("--");
		printSB.append("神器战力：").append(fixEquip).append("--");
		printSB.append("宝石战力：").append(gem).append("--");
		printSB.append("帮派技能战力：").append(gs).append("--");
		printSB.append("技能战力：").append(skill).append("--");
		printSB.append("道术战力：").append(taoist).append("--");
		printSB.append("法宝战力：").append(mf).append("--");
		printSB.append("时装战力：").append(fashionF).append("--");
		// System.err.println(printSB.toString());
		angelArrayLog.info(printSB.toString());

		return fighting;
	}

	private static Logger angelArrayLog = Logger.getLogger("angelArrayLog");
}