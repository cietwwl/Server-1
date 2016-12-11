package com.playerdata.fightinggrowth.fightingfunc;

import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.rwbase.common.IFunction;
import com.rwbase.common.attribute.param.MagicParam.MagicBuilder;
import com.rwbase.dao.item.pojo.ItemData;

public class FSGetMagicFightingOfSingleFunc implements IFunction<Hero, Integer> {

	private static FSGetMagicFightingOfSingleFunc _instance = new FSGetMagicFightingOfSingleFunc();

	public static FSGetMagicFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Hero hero) {
		Player owner = PlayerMgr.getInstance().find(hero.getOwnerUserId());
		if (owner == null) {
			return 0;
		}

		ItemData magic = owner.getMagic();
		if (magic != null) {
			MagicBuilder mb = new MagicBuilder();
			mb.setHeroTemplateId(hero.getTemplateId());
			mb.setMagicId(String.valueOf(magic.getModelId()));
			mb.setMagicLevel(magic.getMagicLevel());
			mb.setMagicAptitude(magic.getMagicAdvanceLevel());
			mb.setIsMainRole(hero.isMainRole());
			return FightingCalcComponentType.MAGIC.calc.calc(mb.build());
		} else {
			GameLog.warn("FSGetMagicFightingOfSingleFunc", owner.getUserId(), "计算法宝战力时，找不到法宝！");
			return 0;
		}
	}

}
