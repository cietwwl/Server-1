package com.playerdata.fightinggrowth.fightingfunc;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.FixEquipLevelFightingCfgDAO;
import com.rwbase.dao.fighting.FixEquipQualityFightingCfgDAO;
import com.rwbase.dao.fighting.FixEquipStarFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.FixEquipLevelFightingCfg;
import com.rwbase.dao.fighting.pojo.FixEquipQualityFightingCfg;
import com.rwbase.dao.fighting.pojo.FixEquipStarFightingCfg;

public class FSGetFixEquipCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	@Override
	public Integer apply(Hero hero) {
		int fighting = 0;
		List<HeroFixEquipInfo> fixEquipInfos = new ArrayList<HeroFixEquipInfo>();
		fixEquipInfos.addAll(hero.getFixExpEquipMgr().getHeroFixSimpleInfo(hero.getId()));
		fixEquipInfos.addAll(hero.getFixNormEquipMgr().getHeroFixSimpleInfo(hero.getId()));
		HeroFixEquipInfo equipInfo;
		FixEquipLevelFightingCfg lvFightingCfg;
		FixEquipQualityFightingCfg qualityFightingCfg;
		FixEquipStarFightingCfg starFightingCfg;
		for (int k = 0; k < fixEquipInfos.size(); k++) {
			equipInfo = fixEquipInfos.get(k);
			lvFightingCfg = FixEquipLevelFightingCfgDAO.getInstance().getByLevel(equipInfo.getLevel());
			qualityFightingCfg = FixEquipQualityFightingCfgDAO.getInstance().getCfgById(String.valueOf(equipInfo.getQuality())); // 神器进阶的战斗力配置
			starFightingCfg = FixEquipStarFightingCfgDAO.getInstance().getCfgById(String.valueOf(equipInfo.getStar())); // 神器觉醒的战斗力配置
			int slotIndex = equipInfo.getSlot() + 1;
			fighting += lvFightingCfg.getFightingOfIndex(slotIndex);
			fighting += qualityFightingCfg.getFightingOfIndex(slotIndex);
			fighting += starFightingCfg.getFightingOfIndex(slotIndex);
		}
		return fighting;
	}

}
