package com.playerdata.fightinggrowth.fightingfunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.Utils;
import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.rwbase.common.IFunction;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;

public class FSGetNormEquipCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {
	
	private static final FSGetNormEquipCurrentFightingOfSingleFunc _instance = new FSGetNormEquipCurrentFightingOfSingleFunc();

	private HeroEquipCfgDAO heroEquipCfgDAO;
	
	protected FSGetNormEquipCurrentFightingOfSingleFunc() {
		heroEquipCfgDAO = HeroEquipCfgDAO.getInstance();
	}
	
	public static final FSGetNormEquipCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Hero hero) {
		List<EquipItem> equipList = hero.getEquipMgr().getEquipList(hero.getId());
		int fighting;
		if (equipList.size() > 0) {
			Map<Integer, Integer> attrMap = new HashMap<Integer, Integer>(heroEquipCfgDAO.getCfgById(String.valueOf(equipList.get(0).getModelId())).getAttrDataMap());
			for (int i = 1, size = equipList.size(); i < size; i++) {
				HeroEquipCfg equipCfg = heroEquipCfgDAO.getCfgById(String.valueOf(equipList.get(i).getModelId()));
				Utils.combineAttrMap(equipCfg.getAttrDataMap(), attrMap);
			}
			fighting = FightingCalculator.calculateFighting(hero.getTemplateId(), attrMap);
		} else {
			fighting = 0;
		}
		return fighting;
	}

}
