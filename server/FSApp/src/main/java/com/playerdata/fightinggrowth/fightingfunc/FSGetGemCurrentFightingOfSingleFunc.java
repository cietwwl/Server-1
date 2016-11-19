package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.GemParam.GemBuilder;

public class FSGetGemCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetGemCurrentFightingOfSingleFunc _instance = new FSGetGemCurrentFightingOfSingleFunc();

	// private GemFightingCfgDAO gemFightingCfgDAO;

	protected FSGetGemCurrentFightingOfSingleFunc() {
		// gemFightingCfgDAO = GemFightingCfgDAO.getInstance();
	}

	public static FSGetGemCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		List<String> gemIdList = hero.getInlayMgr().getInlayGemList(FSHeroMgr.getInstance().getOwnerOfHero(hero), hero.getId());
		if (gemIdList.isEmpty()) {
			return 0;
		}

		GemBuilder gb = new GemBuilder();
		gb.setHeroId(hero.getTemplateId());
		gb.setGemList(gemIdList);

		return FightingCalcComponentType.GEM.calc.calc(gb.build());
		// GemCfg gemCfg;
		// OneToOneTypeFightingCfg gemFightingCfg;
		// int fighting = 0;
		// for (String cfgId : gemIdList) {
		// gemCfg = GemCfgDAO.getInstance().getCfgById(cfgId);
		// gemFightingCfg = gemFightingCfgDAO.getCfgById(String.valueOf(gemCfg.getGemLevel()));
		// fighting += gemFightingCfg.getFighting();
		// }
		// return fighting;
	}
}