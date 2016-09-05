package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.rwbase.common.IFunction;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;

public class FSGetNormEquipCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private HeroEquipCfgDAO heroEquipCfgDAO;
	
	public FSGetNormEquipCurrentFightingOfSingleFunc() {
		heroEquipCfgDAO = HeroEquipCfgDAO.getInstance();
	}
	
	@Override
	public Integer apply(Hero hero) {
		List<EquipItem> equipList = hero.getEquipMgr().getEquipList(hero.getId());
		int fighting = 0;
		for (EquipItem equipItem : equipList) {
			HeroEquipCfg equipCfg = heroEquipCfgDAO.getCfgById(String.valueOf(equipItem.getModelId()));
			fighting += FightingCalculator.calculateFighting(equipCfg.getAttrDataMap(), hero.getTemplateId());
		}
		return fighting;
	}

}
