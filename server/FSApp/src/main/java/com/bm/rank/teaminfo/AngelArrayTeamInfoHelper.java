package com.bm.rank.teaminfo;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.RankType;
import com.bm.rank.anglearray.AngleArrayComparable;
import com.bm.rank.teaminfo.AngelArrayTeamInfoCall.TeamInfoCallback;
import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.army.ArmyHero;
import com.playerdata.army.ArmyInfo;
import com.playerdata.army.ArmyMagic;
import com.playerdata.army.CurAttrData;
import com.playerdata.readonly.HeroIF;
import com.playerdata.readonly.HeroMgrIF;
import com.playerdata.readonly.PlayerIF;
import com.playerdata.team.EquipInfo;
import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroInfo;
import com.playerdata.team.SkillInfo;
import com.playerdata.team.TeamInfo;
import com.rw.fsutil.ranking.Ranking;
import com.rw.fsutil.ranking.RankingEntry;
import com.rw.fsutil.ranking.RankingFactory;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.param.MagicParam;
import com.rwbase.common.attribute.param.MagicParam.MagicBuilder;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.skill.pojo.SkillHelper;

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
		teamInfo.setGroupName(p.getUserGroupAttributeDataMgr().getUserGroupAttributeData().getGroupName());
		teamInfo.setLevel(p.getLevel());
		teamInfo.setUuid(p.getUserId());

		// 法宝信息
		ItemData magic = p.getMagic();
		ArmyMagic magicInfo = new ArmyMagic();
		if (magic != null) {
			magicInfo.setModelId(magic.getModelId());
			magicInfo.setLevel(magic.getMagicLevel());
		}

		teamInfo.setMagic(magicInfo);

		// 英雄的阵容
		int heroSize = teamHeroList.size();

		HeroMgr heroMgr = p.getHeroMgr();
		// 阵容战力
		int fighting = 0;
		// 获取阵容信息
		List<HeroInfo> heroList = new ArrayList<HeroInfo>(heroSize);
		for (int i = 0; i < heroSize; i++) {
			int heroModelId = teamHeroList.get(i);
			Hero hero = heroMgr.getHeroByModerId(heroModelId);
			if (hero == null) {
				continue;
			}

			HeroInfo heroInfo = new HeroInfo();
			// 基础属性
			HeroBaseInfo heroBaseInfo = new HeroBaseInfo();
			heroBaseInfo.setLevel(hero.getLevel());
			heroBaseInfo.setStar(hero.getStarLevel());
			heroBaseInfo.setQuality(hero.getQualityId());
			heroBaseInfo.setTmpId(hero.getTemplateId());

			heroInfo.setBaseInfo(heroBaseInfo);
			// 装备
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

				heroInfo.setEquip(equipInfoList);
			}

			// 宝石
			heroInfo.setGem(hero.getInlayMgr().getInlayGemList());
			// 技能
			List<SkillInfo> skillInfoList = null;

			// int skillLevel = 0;
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

					// skillLevel += skill.getLevel();
				}

				heroInfo.setSkill(skillInfoList);
			}

			heroList.add(heroInfo);
			fighting += hero.getFighting();
		}

		teamInfo.setHero(heroList);
		teamInfo.setTeamFighting(fighting);

		return teamInfo;
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
		List<ArmyHero> armyHeroList = new ArrayList<ArmyHero>(size);
		for (int i = 0; i < size; i++) {
			ArmyHero armyHero = parseHeroInfo2ArmyHero(heroList.get(i), magic);

			if (armyHero.isPlayer()) {
				armyInfo.setPlayer(armyHero);
			} else {
				armyHeroList.add(armyHero);
			}
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
	private static ArmyHero parseHeroInfo2ArmyHero(HeroInfo heroInfo, ArmyMagic magic) {
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
		// AttrData heroAttrData = AttrDataCalcFactory.getHeroAttrData(heroInfo);
		MagicParam magicParam = null;
		if (magic != null) {

			MagicParam.MagicBuilder builder = new MagicBuilder();
			builder.setMagicId(String.valueOf(magic.getModelId()));
			builder.setMagicLevel(magic.getLevel());
			builder.setUserId(tmpId);

			magicParam = builder.build();
		}

		AttrData heroAttrData = AttributeBM.getRobotAttrData(tmpId, heroInfo, magicParam);
		armyHero.setAttrData(heroAttrData);

		// 当前血量
		CurAttrData curAttrData = new CurAttrData();
		curAttrData.setCurLife(heroAttrData.getLife());
		curAttrData.setCurEnergy(heroAttrData.getEnergy());
		armyHero.setCurAttrData(curAttrData);
		// 设置是否是主角
		boolean isPlayer = roleCfg.getRoleType() == 1;
		armyHero.setPlayer(isPlayer);
		// 计算战斗力
		armyHero.setFighting(FightingCalculator.calFighting(tmpId, skillLevel, isPlayer ? magic.getLevel() : 0, isPlayer ? String.valueOf(magic.getModelId()) : "", heroAttrData));

		return armyHero;
	}
}