package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.GemFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;
import com.rwbase.dao.item.GemCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;

public class FSGetGemCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {
	
	private static final FSGetGemCurrentFightingOfSingleFunc _instance = new FSGetGemCurrentFightingOfSingleFunc();
	
	private GemFightingCfgDAO gemFightingCfgDAO;
	
	protected FSGetGemCurrentFightingOfSingleFunc() {
		gemFightingCfgDAO = GemFightingCfgDAO.getInstance();
	}
	
	public static final FSGetGemCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		List<String> gemIdList = hero.getInlayMgr().getInlayGemList(hero.getPlayer(), hero.getId());
		GemCfg gemCfg;
		OneToOneTypeFightingCfg gemFightingCfg;
		int fighting = 0;
		for (String cfgId : gemIdList) {
			gemCfg = GemCfgDAO.getInstance().getCfgById(cfgId);
			gemFightingCfg = gemFightingCfgDAO.getCfgById(String.valueOf(gemCfg.getGemLevel()));
			fighting += gemFightingCfg.getFighting();
		}
		return fighting;
	}

}
