package com.rw.service.skill;

import java.util.List;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.SkillMgr;
import com.playerdata.UserGameDataMgr;
import com.rw.service.dailyActivity.Enum.DailyActivityType;
import com.rwbase.dao.skill.SkillFeeCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillFeeCfg;
import com.rwbase.dao.user.CfgBuySkill;
import com.rwbase.dao.user.CfgBuySkillDAO;
import com.rwproto.PrivilegeProtos.HeroPrivilegeNames;
import com.rwproto.SkillServiceProtos.SkillData;
import com.rwproto.SkillServiceProtos.SkillEventType;
import com.rwproto.SkillServiceProtos.SkillResponse;

public class SkillHandler {

	private SkillHandler() {
	}

	private static SkillHandler instance = new SkillHandler();

	public static SkillHandler getInstance() {
		return instance;
	}

	/**
	 * 查询技能信息
	 * 
	 * @param player
	 * @return
	 */
	public ByteString querySkillInfo(Player player) {
		SkillResponse.Builder r = player.getSkillMgr().getSkillInfo(player);
		r.setEventType(SkillEventType.QUERY_SKILL_INFO);
		return r.build().toByteString();
	}

	public ByteString updateSkill(Player player, String heroId, List<SkillData> skillRequestList) {
		Hero hero = player.getHeroMgr().getHeroById(heroId);
		if (hero == null) {
			return getFailResponse(player, "你还没有拥有这个英雄", SkillEventType.Skill_Upgrade);
		}
		// 获取指定佣兵的SkillMgr
		SkillMgr skillMgr = getSkillMgr(player, heroId);
		if (skillMgr == null) {
			GameLog.error("hero", "updateSkill", player + "获取佣兵失败：" + heroId);
			return getFailResponse(player, SkillConstant.HERO_NOT_EXIST, SkillEventType.Skill_Upgrade);
		}

		SkillFeeCfgDAO skillCfgDAO = SkillFeeCfgDAO.getInstance();
		// 先重置技能点
		skillMgr.reshSkillPoint();
		List<Skill> skillList = skillMgr.getSkillList();
		int totalMoney = 0;
		int totalPoints = 0;
		for (int i = skillRequestList.size(); --i >= 0;) {
			SkillData skillData = skillRequestList.get(i);
			int skillId = skillData.getSkillId();
			int addLevel = skillData.getAdditiveLevel();
			Skill skill = getSkill(skillList, skillId);
			if (skill == null) {
				GameLog.error("hero", "updateSkill", player + "获取技能失败：" + heroId + ",skillId=" + skillId);
				return getFailResponse(player, "找不到指定技能", SkillEventType.Skill_Upgrade);
			}
			for (int add = 0; add < addLevel; add++) {
				SkillFeeCfg skillFeeCfg = skillCfgDAO.getSkillFeeCfg(getRoleType(player, heroId).ordinal(), skill.getOrder(), skill.getLevel() + add);
				// 计算升级总价钱
				totalMoney += skillFeeCfg.getCoin();
			}
			totalPoints += addLevel;
		}
		UserGameDataMgr gameDataMgr = player.getUserGameDataMgr();
		int currentPoints = gameDataMgr.getSkillPointCount();
		if (currentPoints < totalPoints) {
			GameLog.error("hero", "updateSkill", player + "升级技能所需点数不够：" + heroId + ",needPoints = " + totalPoints + ",remainPoints=" + currentPoints);
			return getFailResponse(player, "升级所需技能点不够", SkillEventType.Skill_Upgrade);
		}
		if (gameDataMgr.getCoin() < totalMoney) {
			GameLog.error("hero", "updateSkill", player + "请求增加技能金币不够：needCoin=" + totalMoney + ",coin=" + player.getUserGameDataMgr().getCoin());
			return getFailResponse(player, "升级所需金币不够", SkillEventType.Skill_Upgrade);
		}
		int heroLevel = hero.getLevel();
		for (int i = skillRequestList.size(); --i >= 0;) {
			SkillData skillData = skillRequestList.get(i);
			int skillId = skillData.getSkillId();
			int addLevel = skillData.getAdditiveLevel();
			Skill skill = getSkill(skillList, skillId);
			if (skill == null) {
				GameLog.error("hero", "updateSkill", player + "获取佣兵技能失败：" + heroId + ",skillId = " + skillId);
				continue;
			}
			int skillLevel = skill.getLevel();
			if ((skillLevel + addLevel) > heroLevel) {
				GameLog.error("hero", "updateSkill", player + "请求增加技能等级过高：add=" + addLevel + ",skillLevel=" + skillLevel + ",heroLevel=" + heroLevel);
				continue;
			}
			int costCoin = 0;
			for (int add = 0; add < addLevel; add++) {
				SkillFeeCfg skillFeeCfg = skillCfgDAO.getSkillFeeCfg(getRoleType(player, heroId).ordinal(), skill.getOrder(), skill.getLevel() + add);
				// 计算升级总价钱
				costCoin += skillFeeCfg.getCoin();
			}
			if (gameDataMgr.getCoin() < costCoin) {
				GameLog.error("hero", "updateSkill", player + "请求增加技能金币不够：add=" + addLevel + ",skillLevel=" + skillLevel + ",heroLevel=" + heroLevel + ",needCoin=" + totalMoney + ",coin=" + player.getUserGameDataMgr().getCoin());
				continue;
			}
			if (!skillMgr.updateSkill(skill.getSkillId(), addLevel)) {
				GameLog.error("hero", "updateSkill", player + "升级技能失败：add=" + addLevel + ",skillLevel=" + skillLevel + ",heroLevel=" + heroLevel + ",skillId=" + skillId);
				continue;
			}
			// 扣除金币
			gameDataMgr.addCoin(-costCoin);
		}
		int max = player.getSkillMgr().getMaxSkillCount();
		if (gameDataMgr.getLastRecoverSkillPointTime() == 0 || currentPoints == max) {
			gameDataMgr.setLastRecoverSkillPointTime(System.currentTimeMillis());
		}
		int skillPointCost = currentPoints - totalPoints;
		gameDataMgr.setSkillPointCount(skillPointCost);
		player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Hero_SkillUpgrade, totalPoints);
		
		player.getUserTmpGameDataFlag().setSynFightingAll(true);
		return getFailResponse(player, "", SkillEventType.Skill_Upgrade);
	}

	private ByteString getFailResponse(Player player, String reason, SkillEventType eventType) {
		SkillResponse.Builder response = player.getSkillMgr().getSkillInfo(player);
		if (reason == null) {
			response.setFailReason("");
		} else {
			response.setFailReason(reason);
		}
		response.setEventType(eventType);
		return response.build().toByteString();
	}

	private Skill getSkill(List<Skill> skillList, int id) {
		String prefix = String.valueOf(id);
		for (int i = skillList.size(); --i >= 0;) {
			Skill skill = skillList.get(i);
			if (skill.getSkillId().contains(prefix)) {
				return skill;
			}
		}
		return null;
	}

	// /**
	// * 技能升级
	// *
	// * @param roleId
	// * @param player
	// * @param index
	// * @return
	// */
	// public ByteString skillUpdate(String roleId, Player player, int index) {
	// SkillResponse.Builder response = SkillResponse.newBuilder();
	// response.setEventType(SkillEventType.Skill_Upgrade);
	// SkillMgr pSkillMgr = getSkillMgr(player, roleId);
	// RoleType pRoleType = getRoleType(player, roleId);
	// response.setResultType(SkillResultType.fail);
	// if (pSkillMgr == null) {
	// player.NotifyCommonMsg("角色不存在！");
	// return response.build().toByteString();
	// }
	// Skill skill = pSkillMgr.getSkill(index);
	// if (skill == null) {
	// player.NotifyCommonMsg("技能不存在！");
	// return response.build().toByteString();
	// }
	// if (!pSkillMgr.canUpgradeSkill(skill, true)) {
	// return response.build().toByteString();
	// }
	//
	// SkillFeeCfg skillFeeCfg =
	// SkillFeeCfgDAO.getInstance().getSkillFeeCfg(pRoleType.ordinal(),
	// skill.getOrder(), skill.getLevel());
	// player.getUserGameDataMgr().addCoin(-skillFeeCfg.getCoin());
	// pSkillMgr.upgradeSkill(index);
	// player.getDailyActivityMgr().AddTaskTimesByType(DailyActivityType.Hero_SkillUpgrade,
	// 1);
	// response.setOrder(index);
	// response.setResultType(SkillResultType.succeed);
	// return response.build().toByteString();
	// }

	public ByteString buySkillPoint(Player player) {
//		PrivilegeCfg privilege = PrivilegeCfgDAO.getInstance().getCfg(player.getVip());
		
		boolean openBuySkillPoint = player.getPrivilegeMgr().getBoolPrivilege(HeroPrivilegeNames.isAllowBuySkillPoint);
		// 未开放购买
		if (!openBuySkillPoint) {// 未开放购买getBuySkillPointOpen
			return getFailResponse(player, "VIP等级不足！", SkillEventType.Buy_Skill_Point);
		}

		// 金币不足
		int currTimes = player.getUserGameDataMgr().getBuySkillTimes();
		CfgBuySkill cfgBuySkill = CfgBuySkillDAO.getInstance().getCfgBuySkill(currTimes + 1);// 购买增加技能配置
		int leftGoldNum = player.getUserGameDataMgr().getGold() - cfgBuySkill.getNeedPurse();
		if (leftGoldNum < 0) {
			return getFailResponse(player, "金币不足", SkillEventType.Buy_Skill_Point);
		}

		// 技能点上限
		player.getSkillMgr().buySkillPoint();
		return getFailResponse(player, "", SkillEventType.Buy_Skill_Point);
	}

	private SkillMgr getSkillMgr(Player player, String roleId) {
		if (player.getUserId().equals(roleId)) {
			return player.getSkillMgr();
		}
		Hero pHero = player.getHeroMgr().getHeroById(roleId);
		if (pHero != null) {
			return pHero.getSkillMgr();
		}
		return null;
	}

	private RoleType getRoleType(Player player, String roleId) {
		if (player.getUserId().equals(roleId)) {
			return RoleType.Player;
		}
		return RoleType.Hero;
	}
}

enum RoleType {
	Hero, // 佣兵
	Player, // 角色
};
