package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Player;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.fightinggrowth.calc.param.FashionFightingParam.Builder;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fashion.FashionItem;
import com.rwproto.FashionServiceProtos.FashionType;

public class FSGetFashionCurrentFightingOfSingleFunc implements IFunction<Player, Integer> {
	
	private static final FSGetFashionCurrentFightingOfSingleFunc _instance = new FSGetFashionCurrentFightingOfSingleFunc();

	public static final FSGetFashionCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}
	
	@Override
	public Integer apply(Player player) {
		List<FashionItem> allFashions = player.getFashionMgr().getOwnedFashions();
		if (allFashions.isEmpty()) {
			return 0;
		}

		int suitCount = 0;
		int wingCount = 0;
		int petCount = 0;
		FashionItem temp;
		for (int i = 0, size = allFashions.size(); i < size; i++) {
			temp = allFashions.get(i);
			switch (temp.getType()) {
			case FashionType.Suit_VALUE:
				suitCount++;
				break;
			case FashionType.Pet_VALUE:
				suitCount++;
				break;
			case FashionType.Wing_VALUE:
				wingCount++;
				break;
			}
		}

		Builder b = new Builder();
		b.setSuitCount(suitCount);
		b.setWingCount(wingCount);
		b.setPetCount(petCount);

		return FightingCalcComponentType.FASHION.calc.calc(b.build());
	}

}
