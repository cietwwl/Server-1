package com.playerdata.fightinggrowth.fightingfunc;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.team.EquipInfo;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.EquipParam.EquipBuilder;
import com.rwbase.dao.equipment.EquipItem;

public class FSGetNormEquipCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetNormEquipCurrentFightingOfSingleFunc _instance = new FSGetNormEquipCurrentFightingOfSingleFunc();

	// private HeroEquipCfgDAO heroEquipCfgDAO;

	protected FSGetNormEquipCurrentFightingOfSingleFunc() {
		// heroEquipCfgDAO = HeroEquipCfgDAO.getInstance();
	}

	public static FSGetNormEquipCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		List<EquipItem> equipList = hero.getEquipMgr().getEquipList(hero.getId());
		if (equipList.isEmpty()) {
			return 0;
		}

		int size = equipList.size();
		List<EquipInfo> equipInfoList = new ArrayList<EquipInfo>(size);

		for (int i = 0; i < size; i++) {
			EquipItem equipItem = equipList.get(i);

			EquipInfo info = new EquipInfo();
			info.settId(String.valueOf(equipItem.getModelId()));
			info.seteLevel(equipItem.getLevel());

			equipInfoList.add(info);
		}

		EquipBuilder eb = new EquipBuilder();
		eb.setHeroId(hero.getTemplateId());
		eb.setEquipList(equipInfoList);
		return FightingCalcComponentType.EQUIP.calc.calc(eb.build());
	}
}
