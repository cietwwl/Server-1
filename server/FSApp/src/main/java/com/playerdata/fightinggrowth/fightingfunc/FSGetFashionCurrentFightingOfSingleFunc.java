package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.bm.arena.ArenaRobotDataMgr;
import com.playerdata.Player;
import com.playerdata.fightinggrowth.calc.FightingCalcComponentType;
import com.playerdata.fightinggrowth.calc.param.FashionFightingParam.Builder;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fashion.FashionItem;
import com.rwproto.FashionServiceProtos.FashionType;

public class FSGetFashionCurrentFightingOfSingleFunc implements IFunction<Player, Integer> {

	private static FSGetFashionCurrentFightingOfSingleFunc _instance = new FSGetFashionCurrentFightingOfSingleFunc();

	public static FSGetFashionCurrentFightingOfSingleFunc getInstance() {
		return _instance;
	}

	@Override
	public Integer apply(Player player) {
		boolean robot = player.isRobot();
		Builder b = new Builder();
		if (!robot) {
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

			b.setSuitCount(suitCount);
			b.setWingCount(wingCount);
			b.setPetCount(petCount);
		} else {
			int[] fashionIdArr = ArenaRobotDataMgr.getMgr().getFashionIdArr(player.getUserId());
			if (fashionIdArr != null && fashionIdArr.length >= 3) {
				b.setSuitCount(fashionIdArr[0] > 0 ? 1 : 1);
				b.setWingCount(fashionIdArr[1] > 0 ? 1 : 1);
				b.setPetCount(fashionIdArr[2] > 0 ? 1 : 1);
			}
		}
		return FightingCalcComponentType.FASHION.calc.calc(b.build());
	}
}
