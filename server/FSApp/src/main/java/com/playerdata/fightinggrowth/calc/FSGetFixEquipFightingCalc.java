package com.playerdata.fightinggrowth.calc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.playerdata.fightinggrowth.calc.param.FixEquipFightingParam;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.dao.fighting.FixEquipLevelFightingCfgDAO;
import com.rwbase.dao.fighting.FixEquipQualityFightingCfgDAO;
import com.rwbase.dao.fighting.FixEquipStarFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.FixEquipLevelFightingCfg;
import com.rwbase.dao.fighting.pojo.FixEquipQualityFightingCfg;
import com.rwbase.dao.fighting.pojo.FixEquipStarFightingCfg;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:24:11
 * @desc 神器的战力计算
 **/

public class FSGetFixEquipFightingCalc implements IFightingCalc {

	private Comparator<HeroFixEquipInfo> fixComparator = new Comparator<HeroFixEquipInfo>() {

		@Override
		public int compare(HeroFixEquipInfo o1, HeroFixEquipInfo o2) {
			int id1 = Integer.parseInt(o1.getId());
			int id2 = Integer.parseInt(o2.getId());
			return id1 - id2;
		}
	};

	private FixEquipLevelFightingCfgDAO fixEquipLevelFightingCfgDAO;
	private FixEquipQualityFightingCfgDAO fixEquipQualityFightingCfgDAO;
	private FixEquipStarFightingCfgDAO fixEquipStarFightingCfgDAO;

	protected FSGetFixEquipFightingCalc() {
		fixEquipLevelFightingCfgDAO = FixEquipLevelFightingCfgDAO.getInstance();
		fixEquipQualityFightingCfgDAO = FixEquipQualityFightingCfgDAO.getInstance();
		fixEquipStarFightingCfgDAO = FixEquipStarFightingCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		FixEquipFightingParam fixEquipParam = (FixEquipFightingParam) param;

		List<HeroFixEquipInfo> fixEquips = fixEquipParam.getFixEquips();
		if (fixEquips == null || fixEquips.isEmpty()) {
			return 0;
		}

		Collections.sort(fixEquips, fixComparator);// 排序

		int fighting = 0;
		HeroFixEquipInfo equipInfo;
		FixEquipLevelFightingCfg lvFightingCfg;
		FixEquipQualityFightingCfg qualityFightingCfg;
		FixEquipStarFightingCfg starFightingCfg;

		for (int i = 0, size = fixEquips.size(); i < size; i++) {
			equipInfo = fixEquips.get(i);

			lvFightingCfg = fixEquipLevelFightingCfgDAO.getByLevel(equipInfo.getLevel()); // 神器等级的战斗力配置
			qualityFightingCfg = fixEquipQualityFightingCfgDAO.getCfgById(String.valueOf(equipInfo.getQuality())); // 神器进阶的战斗力配置
			starFightingCfg = fixEquipStarFightingCfgDAO.getCfgById(String.valueOf(equipInfo.getStar())); // 神器觉醒的战斗力配置

			int slotIndex = i + 1; // 战斗力的配置是从1开始，装备是从0开始

			fighting += lvFightingCfg.getFightingOfIndex(slotIndex);
			fighting += qualityFightingCfg.getFightingOfIndex(slotIndex);
			fighting += starFightingCfg.getFightingOfIndex(slotIndex);
		}

		return fighting;
	}
}