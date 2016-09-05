package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fighting.TaoistFightingCfgDAO;
import com.rwbase.dao.fighting.pojo.TaoistFightingCfg;

public class FSGetTaoistCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {
	
	private TaoistMagicCfgHelper taoistMagicCfgHelper;
	private TaoistFightingCfgDAO taoistFightingCfgDAO;
	
	public FSGetTaoistCurrentFightingOfSingleFunc() {
		taoistMagicCfgHelper = TaoistMagicCfgHelper.getInstance();
		taoistFightingCfgDAO = TaoistFightingCfgDAO.getInstance();
	}

	@Override
	public Integer apply(Hero hero) {
		int fighting = 0;
		TaoistFightingCfg taoistFightingCfg;
		List<TaoistMagicCfg> allCfgs = taoistMagicCfgHelper.getAllCfg();
		TaoistMagicCfg taoistMagicCfg;
		Player player = hero.getPlayer();
		for(int i = 0; i < allCfgs.size(); i++) {
			taoistMagicCfg = allCfgs.get(i);
			taoistFightingCfg = taoistFightingCfgDAO.getByLevel(player.getTaoistMgr().getLevel(taoistMagicCfg.getKey()));
			fighting += taoistFightingCfg.getFightingOfIndex(taoistMagicCfg.getTagNum());
		}
		return fighting;
	}

}
