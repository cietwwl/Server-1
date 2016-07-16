package com.playerdata.group;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.bm.group.GroupBM;
import com.bm.rank.teaminfo.AngelArrayTeamInfoCall;
import com.bm.rank.teaminfo.AngelArrayTeamInfoHelper;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.common.PlayerEventListener;
import com.rw.support.FriendSupportFactory;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.enu.eStoreType;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupSkillAttributeCfg;
import com.rwbase.dao.group.pojo.cfg.GroupSkillLevelTemplate;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillAttributeCfgDAO;
import com.rwbase.dao.group.pojo.cfg.dao.GroupSkillLevelCfgDAO;
import com.rwbase.dao.group.pojo.db.GroupSkillItem;
import com.rwbase.dao.group.pojo.db.UserGroupAttributeData;
import com.rwbase.dao.group.pojo.db.dao.UserGroupAttributeDataDAO;
import com.rwbase.dao.group.pojo.db.dao.UserGroupAttributeDataHolder;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;

/*
 * @author HC
 * @date 2016年2月19日 上午10:19:46
 * @Description 个人的帮派数据
 */
public class UserGroupAttributeDataMgr implements PlayerEventListener {

	// private AttrData groupSkillAttrData;// 个人学习技能加成的属性，只存在于内存当中的简单对象
	private UserGroupAttributeDataHolder holder;// 个人帮派数据的管理
	private String userId;// 成员Id

	public UserGroupAttributeDataMgr(String userId) {
		this.userId = userId;
		holder = new UserGroupAttributeDataHolder(userId);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		if (player.isRobot()) {
			return;
		}
		UserGroupAttributeData data = new UserGroupAttributeData();
		data.setUserId(userId);
		data.setGroupId("");
		UserGroupAttributeDataDAO.getDAO().update(data);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		String groupId = userGroupData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return;
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			return;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			return;
		}

		// 检查个人成员信息
		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(userId, false);
		if (memberData == null) {
			return;
		}

		userGroupData.setGroupName(groupData.getGroupName());
		userGroupData.setContribution(memberData.getContribution());
		userGroupData.setDayContribution(memberData.getDayContribution());
		userGroupData.setJoinTime(memberData.getReceiveTime());
	}

	@Override
	public void init(Player player) {
	}

	/**
	 * 获取帮派的个人数据
	 * 
	 * @return
	 */
	public UserGroupAttributeDataIF getUserGroupAttributeData() {
		return holder.getUserGroupData();
	}

	/**
	 * 获取个人的帮贡
	 * 
	 * @return
	 */
	public long getUserGroupContribution() {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return 0;
		}

		Group group = GroupBM.get(userGroupData.getGroupId());
		if (group == null) {
			return 0;
		}

		GroupMemberDataIF memberData = group.getGroupMemberMgr().getMemberData(userId, false);
		if (memberData == null) {
			return 0;
		}

		return memberData.getContribution();
	}

	/**
	 * 获取个人的帮贡
	 * 
	 * @return
	 */
	public void useUserGroupContribution(int offContribution) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return;
		}

		Group group = GroupBM.get(userGroupData.getGroupId());
		if (group == null) {
			return;
		}

		group.getGroupMemberMgr().updateMemberContribution(userId, offContribution, false);
	}

	/**
	 * 当角色有了帮派的时候，设置数据
	 * 
	 * @param player
	 * @param groupId 有帮派的数据
	 */
	public void updateDataWhenHasGroup(Player player, String groupId, String groupName) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setQuitGroupTime(0);
		baseData.clearApplyGroupIdList();// 清除申请队列
		baseData.setGroupId(groupId);
		baseData.setGroupName(groupName);
		baseData.setJoinTime(System.currentTimeMillis());
		// 同步数据
		updateAndSynUserGroupAttributeData(player);
		notifyGroupSkillAttrData(player);
		// 通知好友更改更新帮派名字
		FriendSupportFactory.getSupport().notifyFriendInfoChanged(player);
		player.getStoreMgr().AddStore();
	}

	/**
	 * 当角色被某个帮派拒绝了之后
	 * 
	 * @param player
	 * @param removeGroupId
	 */
	public void updateDataWhenRefuseByGroup(Player player, String removeGroupId) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.removeApplyGroupId(removeGroupId);
		updateAndSynUserGroupAttributeData(player);
	}

	/**
	 * 更新发送邮件的时间
	 * 
	 * @param player 角色
	 * @param sendTime 发送邮件的时间
	 */
	public void updateSendEmailTime(Player player, long sendTime) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setSendEmailTime(sendTime);
		updateAndSynUserGroupAttributeData(player);
	}

	/**
	 * 当角色被踢出帮派或者退出时更新数据
	 * 
	 * @param player
	 * @param quitTime
	 */
	public void updateDataWhenQuitGroup(Player player, long quitTime) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setGroupId("");
		baseData.setGroupName("");
		baseData.setContribution(0);
		baseData.setQuitGroupTime(quitTime);
		updateAndSynUserGroupAttributeData(player);
		notifyGroupSkillAttrData(player);
		// 通知好友更改更新帮派名字
		FriendSupportFactory.getSupport().notifyFriendInfoChanged(player);
		// 通知阵容更新下名字
		AngelArrayTeamInfoHelper.updateRankingEntry(player, AngelArrayTeamInfoCall.groupCall);
		player.getStoreMgr().removeStore(eStoreType.Union.getOrder());
	}

	/**
	 * 更新个人申请帮派的条目数
	 * 
	 * @param resetTime
	 */
	public void updateAndCheckApplyTimes(long resetTime) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.setLastResetApplyTime(resetTime);
		baseData.setGroupApplySize(0);
		holder.flush();
	}

	/**
	 * 更新个人申请帮派的数据
	 * 
	 * @param applyGroupId
	 */
	public void updateApplyGroupData(Player player, String applyGroupId) {
		UserGroupAttributeData baseData = holder.getUserGroupData();
		baseData.addApplyGroupId(applyGroupId);
		baseData.setGroupApplySize(baseData.getGroupApplySize() + 1);
		updateAndSynUserGroupAttributeData(player);
	}

	/**
	 * 更新角色学习技能的数据
	 * 
	 * @param player 角色
	 * @param skillId 技能Id
	 * @param skillLevel 技能等级
	 * @param time <b>【注】如果不设置这个数据，就填入-1</b>
	 * @param state <b>【注】如果不设置这个数据，就填入-1</b>
	 * @return
	 */
	public boolean updateUserGroupDataWhenStudySkill(Player player, int skillId, int skillLevel, long time, int state) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			return false;
		}

		int hasSkillLevel = userGroupData.getStudySkillLevel(skillId);
		if (hasSkillLevel == skillLevel) {
			GameLog.error("学习帮派技能", player.getUserId(), String.format("学习技能Id[%s],技能等级[%s],已经学习的技能等级[%s]", skillId, skillLevel, hasSkillLevel));
			return false;
		}

		if (skillLevel - hasSkillLevel != 1) {
			GameLog.error("学习帮派技能", player.getUserId(), String.format("学习技能Id[%s],技能等级[%s],已经学习的技能等级[%s]。请求学习等级过高", skillId, skillLevel, hasSkillLevel));
			return false;
		}

		userGroupData.addOrUpdateStudySkill(skillId, skillLevel, time, state);
		updateAndSynUserGroupAttributeData(player);
		// holder.incrementSkillVersion();
		// synUserSkillData(player, -1);
		notifyGroupSkillAttrData(player);
		return true;
	}

	/**
	 * 更新并且同步个人帮派数据
	 * 
	 * @param player 角色
	 * @param userGroupAttributeData 个人帮派数据
	 */
	private void updateAndSynUserGroupAttributeData(Player player) {
		holder.flush();
		holder.synData(player);
	}

	/**
	 * 推送个人的帮派数据
	 * 
	 * @param player
	 */
	public void synUserGroupData(Player player) {
		holder.synData(player);
	}

	/**
	 * 通知所有英雄重算属性
	 * 
	 * @param player
	 */
	private void notifyGroupSkillAttrData(Player player) {
		Enumeration<Hero> herosEnumeration = player.getHeroMgr().getHerosEnumeration();
		while (herosEnumeration.hasMoreElements()) {
			Hero hero = herosEnumeration.nextElement();
			if (hero == null) {
				continue;
			}

			hero.getAttrMgr().reCal();
		}
	}

	/**
	 * 更新帮派的名字
	 * 
	 * @param player
	 * @param groupName
	 */
	public void updateGroupName(Player player, String groupName) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		userGroupData.setGroupName(groupName);
		holder.synData(player);
	}

	/**
	 * 更新个人的帮派贡献
	 * 
	 * @param player
	 * @param contribution
	 * @param dayContribution 今天的总捐献数量
	 */
	public void updateContribution(Player player, int contribution, int dayContribution) {
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		userGroupData.setContribution(contribution);
		userGroupData.setDayContribution(dayContribution);
		holder.synData(player);
	}

	// /**
	// *
	// * @return
	// */
	// public synchronized AttrData getGroupSkillAttrData() {
	// if (groupSkillAttrData == null) {
	// groupSkillAttrData = calcGroupSkillAttrData();
	// }
	//
	// return groupSkillAttrData;
	// }
	//
	// public synchronized void updateGroupSkillAttrData() {
	// groupSkillAttrData = calcGroupSkillAttrData();
	// }

	// /**
	// * 获取帮派技能增加属性
	// *
	// * @return
	// */
	// public Map<Integer, AttrData> getGroupSkillAttrData() {
	// Map<Integer, AttrData> attrMap = new HashMap<Integer, AttrData>(2);
	// UserGroupAttributeData userGroupData = holder.getUserGroupData();
	// if (userGroupData == null) {
	// return attrMap;
	// }
	//
	// String groupId = userGroupData.getGroupId();
	// if (StringUtils.isEmpty(groupId)) {// 没有帮派
	// return attrMap;
	// }
	//
	// if (!userGroupData.hasStudySkill()) {
	// return attrMap;
	// }
	//
	// Group group = GroupBM.get(groupId);
	// if (group == null) {
	// return attrMap;
	// }
	//
	// int energy = 0;// 能量值
	// int life = 0;// 生命
	// int attack = 0;// 攻击
	// int physiqueDef = 0;// 体魄防御
	// int spiritDef = 0;// 精神防御
	// int hit = 0;// 命中
	// int dodge = 0;// 闪避
	// int critical = 0;// 暴击率
	// int toughness = 0;// 韧性
	// int resist = 0;// 抵抗
	// int attackHurt = 0;// 攻击伤害
	// int cutHurt = 0;// 伤害减免
	// int criticalHurt = 0;// 暴击伤害提升
	// int cutCritHurt = 0;// 暴击伤害减免
	// int lifeReceive = 0;// 生命回复
	// int energyReceive = 0;// 能量值回复
	// int attackVampire = 0;// 攻击吸血
	// int attackSpeed = 0;// 攻击速度
	// int moveSpeed = 0;// 移动速度
	// int addCure = 0;// 受到治疗效果增加
	// int cutCure = 0;// 受到治疗效果减少
	// int attackPercent = 0;// 攻击百分比
	// int criticalHurtPercent = 0;// 暴击伤害提升百分比
	// int criticalPercent = 0;// 暴击伤害提升百分比
	// int attackVampirePercent = 0; // 吸血百分比
	// int spiritDefPercent = 0;// 法术防御百分比
	// int dodgePercent = 0;// 闪避百分比
	// int physiqueDefPercent = 0;// 物理防御百分比
	// int attackHurtPercent = 0; // 伤害减免百分比
	// int lifePercent = 0;// 生命百分比
	//
	// GroupSkillAttributeCfgDAO cfgDAO = GroupSkillAttributeCfgDAO.getCfgDAO();
	// GroupSkillLevelCfgDAO dao = GroupSkillLevelCfgDAO.getDAO();
	// GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
	// Enumeration<GroupSkillItem> researchSkill = groupData.getResearchSkill();
	// while (researchSkill.hasMoreElements()) {
	// GroupSkillItem skillItem = researchSkill.nextElement();// 帮派研发的技能
	// int skillId = Integer.parseInt(skillItem.getId());
	// int researchLevel = skillItem.getLevel();// 研发到的等级
	// int studySkillLevel = userGroupData.getStudySkillLevel(skillId);
	// studySkillLevel = researchLevel <= studySkillLevel ? researchLevel : studySkillLevel;
	// if (studySkillLevel <= 0) {// 没学习
	// continue;
	// }
	//
	// GroupSkillLevelTemplate tmp = dao.getSkillLevelTemplate(skillId, studySkillLevel);
	// if (tmp == null) {
	// continue;
	// }
	//
	// GroupSkillAttributeCfg skillAttr = cfgDAO.getGroupSkillAttribute(tmp.getAttributeId());
	// if (skillAttr == null) {
	// continue;
	// }
	//
	// energy += skillAttr.getEnergy();// 能量值
	// life += skillAttr.getLife();// 生命
	// attack += skillAttr.getAttack();// 攻击
	// physiqueDef += skillAttr.getPhysiqueDef();// 体魄防御
	// spiritDef += skillAttr.getSpiritDef();// 精神防御
	// hit += skillAttr.getHit();// 命中
	// dodge += skillAttr.getDodge();// 闪避
	// critical += skillAttr.getCritical();// 暴击率
	// toughness += skillAttr.getToughness();// 韧性
	// resist += skillAttr.getResist();// 抵抗
	// attackHurt += skillAttr.getAttackHurt();// 攻击伤害
	// cutHurt += skillAttr.getCutHurt();// 伤害减免
	// criticalHurt += skillAttr.getCriticalHurt();// 暴击伤害提升
	// cutCritHurt += skillAttr.getCutCritHurt();// 暴击伤害减免
	// lifeReceive += skillAttr.getLifeReceive();// 生命回复
	// energyReceive += skillAttr.getEnergyReceive();// 能量值回复
	// attackVampire += skillAttr.getAttackVampire();// 攻击吸血
	// attackSpeed += skillAttr.getAttackSpeed();// 攻击速度
	// moveSpeed += skillAttr.getMoveSpeed();// 移动速度
	// addCure += skillAttr.getAddCure();// 受到治疗效果增加
	// cutCure += skillAttr.getCutCure();// 受到治疗效果减少
	// // //////////////////////////////////////////////百分比
	// attackPercent += skillAttr.getAttackPercent();// 攻击百分比
	// criticalHurtPercent += skillAttr.getCriticalHurtPercent();// 暴击伤害提升百分比
	// criticalPercent += skillAttr.getCriticalPercent();// 暴击伤害提升百分比
	// attackVampirePercent += skillAttr.getAttackVampirePercent(); // 吸血百分比
	// spiritDefPercent += skillAttr.getSpiritDefPercent();// 法术防御百分比
	// dodgePercent += skillAttr.getDodgePercent();// 闪避百分比
	// physiqueDefPercent += skillAttr.getPhysiqueDefPercent();// 物理防御百分比
	// attackHurtPercent += skillAttr.getAttackHurtPercent(); // 伤害减免百分比
	// lifePercent += skillAttr.getLifePercent();// 生命百分比
	// }
	//
	// AttrData attrData = new AttrData();
	// attrData.setEnergy(energy);
	// attrData.setLife(life);
	// attrData.setAttack(attack);
	// attrData.setPhysiqueDef(physiqueDef);
	// attrData.setSpiritDef(spiritDef);
	// attrData.setHit(hit);
	// attrData.setDodge(dodge);
	// attrData.setCritical(critical);
	// attrData.setToughness(toughness);
	// attrData.setResist(resist);
	// attrData.setAttackHurt(attackHurt);
	// attrData.setCutHurt(cutHurt);
	// attrData.setCriticalHurt(criticalHurt);
	// attrData.setCutCritHurt(cutCritHurt);
	// attrData.setLifeReceive(lifeReceive);
	// attrData.setEnergyReceive(energyReceive);
	// attrData.setAttackVampire(attackVampire);
	// attrData.setAttackSpeed(attackSpeed);
	// attrData.setMoveSpeed(moveSpeed);
	// attrData.setAddCure(addCure);
	// attrData.setCutCure(cutCure);
	// attrMap.put(AttrDataType.ATTR_DATA_TYPE.type, attrData);
	//
	// AttrData precentAttrData = new AttrData();
	// precentAttrData.setAttack(attackPercent);
	// precentAttrData.setAttackHurt(attackHurtPercent);
	// precentAttrData.setAttackVampire(attackVampirePercent);
	// precentAttrData.setCritical(criticalPercent);
	// precentAttrData.setCriticalHurt(criticalHurtPercent);
	// precentAttrData.setSpiritDef(spiritDefPercent);
	// precentAttrData.setDodge(dodgePercent);
	// precentAttrData.setPhysiqueDef(physiqueDefPercent);
	// precentAttrData.setLife(lifePercent);
	// attrMap.put(AttrDataType.ATTR_DATA_PRECENT_TYPE.type, precentAttrData);
	//
	// return attrMap;
	// }

	/**
	 * 获取帮派增加的属性
	 * 
	 * @return
	 */
	public Map<Integer, AttributeItem> getGroupSkillAttrDataMap() {
		HashMap<Integer, AttributeItem> map = new HashMap<Integer, AttributeItem>();
		UserGroupAttributeData userGroupData = holder.getUserGroupData();
		if (userGroupData == null) {
			// GameLog.error("计算英雄帮派属性", userId, "角色没有对应的UserGroupAttributeData数据");
			return map;
		}

		String groupId = userGroupData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {// 没有帮派
			// GameLog.error("计算英雄帮派属性", userId, "角色没有帮派");
			return map;
		}

		if (!userGroupData.hasStudySkill()) {
			// GameLog.error("计算英雄帮派属性", userId, "角色没有学习过任何技能");
			return map;
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			// GameLog.error("计算英雄帮派属性", userId, String.format("[%s]的帮派没有找到数据", groupId));
			return map;
		}

		GroupSkillAttributeCfgDAO cfgDAO = GroupSkillAttributeCfgDAO.getCfgDAO();
		GroupSkillLevelCfgDAO dao = GroupSkillLevelCfgDAO.getDAO();
		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		Enumeration<GroupSkillItem> researchSkill = groupData.getResearchSkill();
		while (researchSkill.hasMoreElements()) {
			GroupSkillItem skillItem = researchSkill.nextElement();// 帮派研发的技能
			int skillId = Integer.parseInt(skillItem.getId());
			int researchLevel = skillItem.getLevel();// 研发到的等级
			int studySkillLevel = userGroupData.getStudySkillLevel(skillId);
			studySkillLevel = researchLevel <= studySkillLevel ? researchLevel : studySkillLevel;
			if (studySkillLevel <= 0) {// 没学习
				continue;
			}

			GroupSkillLevelTemplate tmp = dao.getSkillLevelTemplate(skillId, studySkillLevel);
			if (tmp == null) {
				continue;
			}

			GroupSkillAttributeCfg skillAttr = cfgDAO.getGroupSkillAttribute(tmp.getAttributeId());
			if (skillAttr == null) {
				continue;
			}

			AttributeUtils.calcAttribute(skillAttr.getAttrDataMap(), skillAttr.getPrecentAttrDataMap(), map);
		}

		return map;
	}
	// /**
	// * 推送个人帮派学习技能的数据
	// *
	// * @param player
	// * @param version
	// */
	// public void synUserSkillData(Player player, int version) {
	// holder.synSkillData(player, version);
	// }

}