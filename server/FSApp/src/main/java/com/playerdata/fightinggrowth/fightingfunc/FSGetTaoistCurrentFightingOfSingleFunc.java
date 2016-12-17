package com.playerdata.fightinggrowth.fightingfunc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.bm.arena.ArenaRobotDataMgr;
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
		boolean robot = player.isRobot();
		boolean open = robot ? true : _openLevelLimitDAO.isOpen(eOpenLevelType.TAOIST, player);
		if (open) {
			Map<Integer, Integer> taoistMap = new HashMap<Integer, Integer>();
			if (!robot) {
				Iterable<Entry<Integer, Integer>> taoistList = player.getTaoistMgr().getAllTaoist();
				for (Iterator<Map.Entry<Integer, Integer>> itr = taoistList.iterator(); itr.hasNext();) {
					Map.Entry<Integer, Integer> entry = itr.next();
					taoistMap.put(entry.getKey(), entry.getValue());
				}
			} else {
				taoistMap = ArenaRobotDataMgr.getMgr().getRobotTaoistMap(player.getUserId());
			}

			TaoistBuilder builder = new TaoistBuilder();
			builder.setHeroId(hero.getTemplateId());
			builder.setTaoistMap(taoistMap);
			return FightingCalcComponentType.TAOIST.calc.calc(builder.build());
		}
		return 0;
	}

}
