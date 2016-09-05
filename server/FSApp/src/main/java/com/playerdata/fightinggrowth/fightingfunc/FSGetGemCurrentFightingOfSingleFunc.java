package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.GemFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;

public class FSGetGemCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {
	
	private GemFightingCfgDAO gemFightingCfgDAO;
	
	public FSGetGemCurrentFightingOfSingleFunc() {
		gemFightingCfgDAO = GemFightingCfgDAO.getInstance();
	}

	@Override
	public Integer apply(Hero hero) {
		List<String> gemIdList = hero.getInlayMgr().getInlayGemList(hero.getPlayer(), hero.getId());
		GemCfg gemCfg;
		OneToOneTypeFightingCfg gemFightingCfg;
		int fighting = 0;
		for (String cfgId : gemIdList) {
			gemCfg = GemCfgDAO.getInstance().getCfgById(cfgId);
			gemFightingCfg = gemFightingCfgDAO.getByRequiredLv(gemCfg.getLevel());
			fighting += gemFightingCfg.getFighting();
		}
		return fighting;
	}

}
