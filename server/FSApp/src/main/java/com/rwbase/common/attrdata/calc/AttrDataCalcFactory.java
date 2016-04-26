package com.rwbase.common.attrdata.calc;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.team.HeroBaseInfo;
import com.playerdata.team.HeroInfo;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

/*
 * @author HC
 * @date 2016年4月15日 下午9:57:37
 * @Description 
 */
public class AttrDataCalcFactory {
	private static AttrDataCalcFactory instance = new AttrDataCalcFactory();

	public static AttrDataCalcFactory getInstance() {
		return instance;
	}

	private AttrDataCalcFactory() {
	}

	/**
	 * 获取英雄的属性
	 * 
	 * @param heroInfo
	 * @return
	 */
	public static AttrData getHeroAttrData(HeroInfo heroInfo) {
		if (heroInfo == null) {
			return null;
		}

		HeroBaseInfo baseInfo = heroInfo.getBaseInfo();
		if (baseInfo == null) {
			return null;
		}

		String tmpId = baseInfo.getTmpId();
		RoleCfg heroCfg = RoleCfgDAO.getInstance().getCfgById(tmpId);
		if (heroCfg == null) {
			return null;
		}

		AttrData baseAttrData = new AttrData();
		AttrData precentAttrData = new AttrData();

		List<IAttrDataCalc> attrDataCalcList = new ArrayList<IAttrDataCalc>();

		RoleBaseAttrDataCalc roleBaseAttrDataCalc = new RoleBaseAttrDataCalc(baseInfo);// 基础属性
		attrDataCalcList.add(roleBaseAttrDataCalc);

		EquipAttrDataCalc equipAttrDataCalc = new EquipAttrDataCalc(heroInfo.getEquip());// 装备属性
		attrDataCalcList.add(equipAttrDataCalc);

		SkillAttrDataCalc skillAttrDataCalc = new SkillAttrDataCalc(heroInfo.getSkill());// 技能属性
		attrDataCalcList.add(skillAttrDataCalc);

		GemAttrDataCalc gemAttrDataCalc = new GemAttrDataCalc(heroInfo.getGem(), String.valueOf(heroCfg.getModelId()));// 宝石属性
		attrDataCalcList.add(gemAttrDataCalc);

		// 计算属性
		for (int i = 0, size = attrDataCalcList.size(); i < size; i++) {
			IAttrDataCalc attrDataCalc = attrDataCalcList.get(i);
			if (attrDataCalc == null) {
				continue;
			}

			AttrData attrDataValue = attrDataCalc.getAttrData();
			if (attrDataValue != null) {
				baseAttrData.plus(attrDataValue);
			}

			AttrData precentAttrDataValue = attrDataCalc.getPrecentAttrData();
			if (precentAttrDataValue != null) {
				precentAttrData.plus(precentAttrDataValue);
			}
		}

		// 整合属性
		return baseAttrData.addPercent(precentAttrData);
	}
}