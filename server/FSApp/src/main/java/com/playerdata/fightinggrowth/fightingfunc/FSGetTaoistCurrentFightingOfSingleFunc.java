package com.playerdata.fightinggrowth.fightingfunc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.TaoistParam.TaoistBuilder;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FSGetTaoistCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetTaoistCurrentFightingOfSingleFunc _instance = new FSGetTaoistCurrentFightingOfSingleFunc();

	// private TaoistMagicCfgHelper taoistMagicCfgHelper;
	// private TaoistFightingCfgDAO taoistFightingCfgDAO;
	private CfgOpenLevelLimitDAO _openLevelLimitDAO;

	protected FSGetTaoistCurrentFightingOfSingleFunc() {
		// taoistMagicCfgHelper = TaoistMagicCfgHelper.getInstance();
		// taoistFightingCfgDAO = TaoistFightingCfgDAO.getInstance();
		_openLevelLimitDAO = CfgOpenLevelLimitDAO.getInstance();
	}

	public static FSGetTaoistCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		Player player = FSHeroMgr.getInstance().getOwnerOfHero(hero);
		if (_openLevelLimitDAO.isOpen(eOpenLevelType.TAOIST, player)) {
			// int fighting = 0;
			// TaoistFightingCfg taoistFightingCfg;
			// TaoistMagicCfg taoistMagicCfg;
			// Iterable<Map.Entry<Integer, Integer>> taoistList = player.getTaoistMgr().getAllTaoist();
			// for (Iterator<Map.Entry<Integer, Integer>> itr = taoistList.iterator(); itr.hasNext();) {
			// Map.Entry<Integer, Integer> entry = itr.next();
			// taoistFightingCfg = taoistFightingCfgDAO.getByLevel(entry.getValue());
			// taoistMagicCfg = taoistMagicCfgHelper.getCfgById(String.valueOf(entry.getKey()));
			// fighting += taoistFightingCfg.getFightingOfIndex(taoistMagicCfg.getTagNum());
			// }
			// return fighting;

			Map<Integer, Integer> taoistMap = new HashMap<Integer, Integer>();
			Iterable<Entry<Integer, Integer>> taoistList = player.getTaoistMgr().getAllTaoist();
			for (Iterator<Map.Entry<Integer, Integer>> itr = taoistList.iterator(); itr.hasNext();) {
				Map.Entry<Integer, Integer> entry = itr.next();
				taoistMap.put(entry.getKey(), entry.getValue());
			}

			TaoistBuilder builder = new TaoistBuilder();
			builder.setHeroId(hero.getTemplateId());
			builder.setTaoistMap(taoistMap);

			return FightingCalcComponentType.TAOIST.calc.calc(builder.build());
		}
		return 0;
	}

}
