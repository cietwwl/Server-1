package com.playerdata.fightinggrowth.calc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.Utils;
import com.log.GameLog;
import com.playerdata.FightingCalculator;
import com.playerdata.team.EquipInfo;
import com.rwbase.common.attribute.param.EquipParam;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;

/**
 * @Author HC
 * @date 2016年10月25日 上午11:48:50
 * @desc 获取装备的战斗力
 **/

public class FSGetEquipFightingCalc implements IFightingCalc {

	private HeroEquipCfgDAO heroEquipCfgDAO;

	protected FSGetEquipFightingCalc() {
		heroEquipCfgDAO = HeroEquipCfgDAO.getInstance();
	}

	@Override
	public int calc(Object param) {
		EquipParam equipParam = (EquipParam) param;

		List<EquipInfo> equipList = equipParam.getEquipList();
		if (equipList == null || equipList.isEmpty()) {
			return 0;
		}

		int fighting = 0;

		int size = equipList.size();
		Map<Integer, Integer> attrMap = new HashMap<Integer, Integer>(size);

		for (int i = 0; i < size; i++) {
			EquipInfo equipInfo = equipList.get(i);
			String equipId = equipInfo.gettId();
			HeroEquipCfg equipCfg = heroEquipCfgDAO.getCfgById(equipId);

			if (equipCfg == null) {
				GameLog.error("FSGetEquipFightingCalc", equipId, "HeroEquipCfgDAO找不到装备配置，id：" + equipId);
				continue;
			}

			Utils.combineAttrMap(equipCfg.getAttrDataMap(), attrMap);
		}

		fighting = FightingCalculator.calculateFighting(equipParam.getHeroId(), attrMap);

		return fighting;
	}
}