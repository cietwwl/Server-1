package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FSGetTaoistCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {

	private static FSGetTaoistCurrentFightingFunc _instance = new FSGetTaoistCurrentFightingFunc();

	private IFunction<Hero, Integer> _single;

	private CfgOpenLevelLimitDAO _cfgOpenLevelLimitDAO;

	protected FSGetTaoistCurrentFightingFunc() {
		this._single = FSGetTaoistCurrentFightingOfSingleFunc.getInstance();
		this._cfgOpenLevelLimitDAO = CfgOpenLevelLimitDAO.getInstance();
	}

	public static FSGetTaoistCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		boolean open = player.isRobot() ? true : _cfgOpenLevelLimitDAO.isOpen(eOpenLevelType.TAOIST, player);
		if (open) {
			int fighting = _single.apply(player.getMainRoleHero()) * teamHeros.size();
			return fighting;
		} else {
			return 0;
		}
	}

}
