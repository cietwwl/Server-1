package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.fightinggrowth.calc.param.FettersFightingParam.Builder;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fetters.pojo.SynFettersData;

public class FSGetFetterCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static final FSGetFetterCurrentFightingOfSingleFunc _instance = new FSGetFetterCurrentFightingOfSingleFunc();

	public static final FSGetFetterCurrentFightingOfSingleFunc getInstnce() {
		return _instance;
	}

	protected FSGetFetterCurrentFightingOfSingleFunc() {
	}

	@Override
	public Integer apply(Hero hero) {
		Player player = FSHeroMgr.getInstance().getOwnerOfHero(hero);

		Builder b = new Builder();

		if (hero.isMainRole()) {
			b.setMagicFetters(player.getMe_FetterMgr().getMagicFetter());
		} else {
			SynFettersData fetterDatas = player.getHeroFettersByModelId(hero.getModeId());
			if (fetterDatas != null) {
				b.setHeroFetters(fetterDatas.getOpenList());
			}
		}

		b.setFixEquipFetters(player.getMe_FetterMgr().getHeroFixEqiupFetter(hero.getModeId()));

		return FightingCalcComponentType.FETTERS.calc.calc(b.build());
	}
}