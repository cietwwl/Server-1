package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rw.service.group.helper.GroupHelper;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;

public class FSGetGroupSkillCurrentFightingFunc implements IBIFunction<Player, List<Hero>, Integer> {

	private static FSGetGroupSkillCurrentFightingFunc _instance = new FSGetGroupSkillCurrentFightingFunc();

	private IFunction<Hero, Integer> _single;

	protected FSGetGroupSkillCurrentFightingFunc() {
		_single = FSGetGroupSkillFightingOfSingleFunc.getInstance();
	}

	public static FSGetGroupSkillCurrentFightingFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player, List<Hero> teamHeros) {
		if (GroupHelper.getInstance().hasGroup(player.getUserId())) {
			return _single.apply(player.getMainRoleHero()) * teamHeros.size();
		} else {
			return 0;
		}
	}

}
