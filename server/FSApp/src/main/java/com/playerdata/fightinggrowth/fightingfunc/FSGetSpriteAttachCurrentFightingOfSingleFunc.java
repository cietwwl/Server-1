package com.playerdata.fightinggrowth.fightingfunc;

import com.playerdata.Hero;
import com.playerdata.SpriteAttachMgr;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.SpriteAttachParam.SpriteAttachBuilder;

public class FSGetSpriteAttachCurrentFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetSpriteAttachCurrentFightingOfSingleFunc _instance = new FSGetSpriteAttachCurrentFightingOfSingleFunc();

	// private SpriteAttachFightingCfgDAO spriteAttachFightingCfgDAO;

	protected FSGetSpriteAttachCurrentFightingOfSingleFunc() {
		// spriteAttachFightingCfgDAO = SpriteAttachFightingCfgDAO.getInstance();
	}

	public static FSGetSpriteAttachCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		SpriteAttachBuilder sab = new SpriteAttachBuilder();
		sab.setHeroId(String.valueOf(hero.getModeId()));
		sab.setItems(SpriteAttachMgr.getInstance().getSpriteAttachHolder().getSpriteAttachItemList(hero.getUUId()));

		return FightingCalcComponentType.SPRITE_ATTACH.calc.calc(sab.build());
	}

}
