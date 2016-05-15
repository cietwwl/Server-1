package com.rwbase.common.attrdata;

import com.common.BeanOperationHelper;
import com.log.GameLog;
import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.common.attrdata.RoleAttrData.Builder;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeCalculator;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class RoleAttrDataHolder {

	final private eSynType roleAttrSynType = eSynType.ROLE_ATTR_ITEM;

	private RoleAttrData heroAttrData;

	private Hero hero;

	private Player player;

	private AttributeCalculator<AttrData> calc;

	public RoleAttrDataHolder(Player playerP, Hero heroP) {
		player = playerP;
		hero = heroP;
		calc = AttributeBM.getAttributeCalculator(player.getUserId(), hero.getUUId());
	}

	public RoleAttrData get() {
		if (heroAttrData == null) {
			calc.updateAttribute();
			heroAttrData = toAttrData();
		}

		return heroAttrData;
	}

	public RoleAttrData reCal() {
		// 重新计算属性
		calc.updateAttribute();
		heroAttrData = toAttrData();

		syn(-1);
		// 设置player战力改变
		if (player != null) {
			player.getTempAttribute().setHeroFightingChanged();
			player.getUserTmpGameDataFlag().setSynFightingAll(true);
		}

		return heroAttrData;
	}

	public void syn(int version) {
		RoleAttrData heroAttrDataTmp = get();
		ClientDataSynMgr.synData(player, heroAttrDataTmp, roleAttrSynType, eSynOpType.UPDATE_SINGLE);
	}

	private RoleAttrData toAttrData() {
		RoleAttrData.Builder builder = new Builder();
		builder.setHeroId(hero.getUUId());
		AttrData baseData = calc.getBaseResult();
		builder.setRoleBaseTotalData(baseData);

		StringBuilder sb = new StringBuilder();

		String baseAttrDesc = BeanOperationHelper.getPositiveValueDiscription(baseData);
		sb.append("角色固定值总属性>>>>>-").append(baseAttrDesc).append("\n");
		GameLog.info("角色的固定总属性", "固定总属性", baseAttrDesc, null);

		AttrData totalData = calc.getResult();
		builder.setTotalData(totalData);

		String totalAttrDesc = BeanOperationHelper.getPositiveValueDiscription(totalData);
		sb.append("角色总属性>>>>>-").append(totalAttrDesc);
		GameLog.info("角色的总属性", "总属性", totalAttrDesc, null);

		builder.setLog(sb.toString());

		// int oldFighting = heroAttrData == null ? 0 : heroAttrData.getFighting();
		int calFighting = FightingCalculator.calFighting(hero, totalData);
		builder.setFighting(calFighting);
		// TODO HC 现在战力先不通知羁绊
		// if (oldFighting > 0 && oldFighting < calFighting) {
		// FettersBM.whenHeroChange(hero.getPlayer(), hero.getModelId());
		// }

		return builder.build();
	}

	// private RoleAttrData toAttrData(Hero pRole) {
	// String log = "";
	// // 固定值属性
	//
	// AttrData roleBaseTotalData = pRole.getRoleBaseInfoMgr().getTotalBaseAttrData();
	// log += "[基础属性（固定值）（存在品阶属性）]-" + BeanOperationHelper.getPositiveValueDiscription(roleBaseTotalData) + "\n";
	//
	// AttrData qualityTotalDataForLog = pRole.getRoleBaseInfoMgr().getTotalQualityAttrDataForLog();
	// log += "[品阶属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(qualityTotalDataForLog) + "\n";
	//
	// AttrData equipTotalData = pRole.getEquipMgr().getTotalEquipAttrData();
	// log += "[装备属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(equipTotalData) + "\n";
	//
	// AttrData skillTotalData = pRole.getSkillMgr().getTotalSkillAttrData();
	// log += "[技能属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(skillTotalData) + "\n";
	//
	// AttrData inlayTotalData = pRole.getInlayMgr().getTotalInlayAttrData();
	// AttrData inlayPercentTotalData = pRole.getInlayMgr().getTotalInlayPercentAttrData();
	// log += "[宝石属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(inlayTotalData) + "\n";
	// log += "[宝石属性（万分比值）]-" + BeanOperationHelper.getPositiveValueDiscription(inlayPercentTotalData) + "\n";
	//
	// // 万分比属性
	// AttrData percentTotalData = new AttrData();
	// percentTotalData.plus(inlayPercentTotalData);
	//
	// // 主角才有时装属性
	// AttrData fashionTotalData = null;
	// if (hero.isMainRole()) {
	// IEffectCfg eff = player.getFashionMgr().getEffectData();
	// fashionTotalData = new AttrData();
	// BeanCopyer.copy(eff.getAddedValues(), fashionTotalData);
	// percentTotalData.plus(eff.getAddedPercentages());
	// log += "[时装属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(fashionTotalData) + "\n";
	// log += "[时装属性（万分比值）]-" + BeanOperationHelper.getPositiveValueDiscription(eff.getAddedPercentages()) + "\n";
	// }
	//
	// // 帮派技能属性加成
	// Map<Integer, AttrData> groupSkillAttrDataMap = player.getUserGroupAttributeDataMgr().getGroupSkillAttrData();
	// AttrData groupSkillAttrData = groupSkillAttrDataMap.get(AttrDataType.ATTR_DATA_TYPE.type);
	// AttrData groupSkillPercentAttrData = groupSkillAttrDataMap.get(AttrDataType.ATTR_DATA_PRECENT_TYPE.type);
	// percentTotalData.plus(groupSkillPercentAttrData);
	// log += "[帮派技能（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(groupSkillAttrData) + "\n";
	// log += "[帮派技能（万份比值）]-" + BeanOperationHelper.getPositiveValueDiscription(groupSkillPercentAttrData) + "\n";
	//
	// // 羁绊属性加成
	// AttrData fettersAttrData = null;
	// AttrData fettersPrecentAttrData = null;
	// SynFettersData heroFetters = hero.getPlayer().getHeroFettersByModelId(hero.getModelId());
	// if (heroFetters != null) {
	// Map<Integer, AttrData> calcHeroFettersAttr = FettersBM.calcHeroFettersAttr(heroFetters.getOpenList());
	// if (calcHeroFettersAttr != null) {
	// fettersAttrData = calcHeroFettersAttr.get(AttrDataType.ATTR_DATA_TYPE.type);
	// fettersPrecentAttrData = calcHeroFettersAttr.get(AttrDataType.ATTR_DATA_PRECENT_TYPE.type);
	// if (fettersPrecentAttrData != null) {
	// percentTotalData.plus(fettersPrecentAttrData);
	// }
	// }
	// }
	// log += "[羁绊（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(fettersAttrData) + "\n";
	// log += "[羁绊（万份比值）]-" + BeanOperationHelper.getPositiveValueDiscription(fettersPrecentAttrData) + "\n";
	//
	// RoleAttrData roleAttrData = new RoleAttrData(pRole.getUUId(), equipTotalData, inlayTotalData, roleBaseTotalData, skillTotalData,
	// fashionTotalData, groupSkillAttrData, fettersAttrData);
	// AttrData totalData = roleAttrData.getTotalData();
	// log += "[总属性（固定值）]-" + BeanOperationHelper.getPositiveValueDiscription(totalData) + "\n";
	// log += "[总属性（万分比值）]-" + BeanOperationHelper.getPositiveValueDiscription(percentTotalData) + "\n";
	//
	// totalData.addPercent(percentTotalData);
	// int calFighting = FightingCalculator.calFighting(pRole, totalData);
	// int oldFighting = roleAttrData.getFighting();
	// roleAttrData.setFighting(calFighting);
	// // 战力修改
	// if (oldFighting > 0 && oldFighting < calFighting) {
	// FettersBM.whenHeroChange(hero.getPlayer(), hero.getModelId());
	// }
	// log += "[总属性（固定值 + 万分比值）]-" + BeanOperationHelper.getPositiveValueDiscription(totalData) + "\n";
	//
	// roleAttrData.setLog(log);
	// return roleAttrData;
	// }
}