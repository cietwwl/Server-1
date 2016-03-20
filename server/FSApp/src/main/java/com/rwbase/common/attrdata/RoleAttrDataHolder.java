package com.rwbase.common.attrdata;

import java.util.Map;

import com.common.BeanOperationHelper;
import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.group.UserGroupAttributeDataMgr.GroupSkillAttrType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class RoleAttrDataHolder {

	final private eSynType roleAttrSynType = eSynType.ROLE_ATTR_ITEM;

	private RoleAttrData heroAttrData;

	private Hero hero;

	private Player player;

	public RoleAttrDataHolder(Player playerP, Hero heroP) {
		player = playerP;
		hero = heroP;
	}

	public RoleAttrData get() {
		if (heroAttrData == null) {
			heroAttrData = toAttrData(hero);
		}

		return heroAttrData;
	}

	public RoleAttrData reCal() {

		heroAttrData = toAttrData(hero);
		syn(-1);
		// 设置player战力改变
		if (player != null) {
			player.getTempAttribute().setHeroFightingChanged();
		}
		return heroAttrData;
	}

	public void syn(int version) {
		RoleAttrData heroAttrDataTmp = get();

		ClientDataSynMgr.synData(player, heroAttrDataTmp, roleAttrSynType, eSynOpType.UPDATE_SINGLE);

	}

	private RoleAttrData toAttrData(Hero pRole) {
		String log = "";
		// 固定值属性

		AttrData roleBaseTotalData = pRole.getRoleBaseInfoMgr().getTotalBaseAttrData();
		log += "[基础属性（固定值）（存在品阶属性）]-" + BeanOperationHelper.getPositiveValueDiscription(roleBaseTotalData) + "\n";

		AttrData qualityTotalDataForLog = pRole.getRoleBaseInfoMgr().getTotalQualityAttrDataForLog();
		log += "[品阶属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(qualityTotalDataForLog) + "\n";

		AttrData equipTotalData = pRole.getEquipMgr().getTotalEquipAttrData();
		log += "[装备属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(equipTotalData) + "\n";

		AttrData skillTotalData = pRole.getSkillMgr().getTotalSkillAttrData();
		log += "[技能属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(skillTotalData) + "\n";

		AttrData inlayTotalData = pRole.getInlayMgr().getTotalInlayAttrData();
		AttrData inlayPercentTotalData = pRole.getInlayMgr().getTotalInlayPercentAttrData();
		log += "[宝石属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(inlayTotalData) + "\n";
		log += "[宝石属性（万分比值）]-" + BeanOperationHelper.getPositiveValueDiscription(inlayPercentTotalData) + "\n";

		// 万分比属性
		AttrData percentTotalData = new AttrData();
		percentTotalData.plus(inlayPercentTotalData);

		// 主角才有时装属性
		AttrData fashionTotalData = null;
		if (hero.isMainRole()) {
			fashionTotalData = player.getFashionMgr().getAttrData();
			AttrData fashionPercentTotalData = player.getFashionMgr().getPercentAttrData();
			percentTotalData.plus(fashionPercentTotalData);
			log += "[时装属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(fashionTotalData) + "\n";
			log += "[时装属性（万分比值）]-" + BeanOperationHelper.getPositiveValueDiscription(fashionPercentTotalData) + "\n";
		}

		// 帮派技能属性加成
		Map<Integer, AttrData> groupSkillAttrDataMap = player.getUserGroupAttributeDataMgr().getGroupSkillAttrData();
		AttrData groupSkillAttrData = groupSkillAttrDataMap.get(GroupSkillAttrType.GROUP_SKILL_ATTR.type);
		AttrData groupSkillPercentAttrData = groupSkillAttrDataMap.get(GroupSkillAttrType.GROUP_SKILL_PRECENT_ATTR.type);
		percentTotalData.plus(groupSkillPercentAttrData);
		log += "[帮派技能（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(groupSkillAttrData) + "\n";
		log += "[帮派技能（万份比值）]-" + BeanOperationHelper.getPositiveValueDiscription(groupSkillPercentAttrData) + "\n";

		RoleAttrData roleAttrData = new RoleAttrData(pRole.getUUId(), equipTotalData, inlayTotalData, roleBaseTotalData, skillTotalData, fashionTotalData, groupSkillAttrData);
		AttrData totalData = roleAttrData.getTotalData();
		log += "[总属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(totalData) + "\n";
		log += "[总属性（万分比值）]-" + BeanOperationHelper.getPositiveValueDiscription(percentTotalData) + "\n";

		totalData.addPercent(percentTotalData);
		roleAttrData.setFighting(FightingCalculator.calFighting(pRole, totalData));
		log += "[总属性（固定值 + 万分比值）]-" + BeanOperationHelper.getPositiveValueDiscription(totalData) + "\n";

		roleAttrData.setLog(log);
		return roleAttrData;
	}

}
