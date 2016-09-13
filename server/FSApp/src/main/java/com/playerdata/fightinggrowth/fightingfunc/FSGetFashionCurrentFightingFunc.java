package com.playerdata.fightinggrowth.fightingfunc;

import java.util.List;

import com.playerdata.Player;
import com.rwbase.common.IFunction;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fighting.FashionFightingCfgDAO;
import com.rwproto.FashionServiceProtos.FashionType;

public class FSGetFashionCurrentFightingFunc implements IFunction<Player, Integer> {
	
	private static final FSGetFashionCurrentFightingFunc _INSTANCE = new FSGetFashionCurrentFightingFunc();
	
	private FashionFightingCfgDAO _fashionFightingCfgDAO;
	
	protected FSGetFashionCurrentFightingFunc() {
		_fashionFightingCfgDAO = FashionFightingCfgDAO.getInstance();
	}
	
	public static final FSGetFashionCurrentFightingFunc getInstance() {
		return _INSTANCE;
	}

	@Override
	public Integer apply(Player player) {
		List<FashionItem> allFashions = player.getFashionMgr().getOwnedFashions();
		int fighting = 0;
		if (allFashions.size() > 0) {
			int suitCount = 0;
			int wingCount = 0;
			int petCount = 0;
			FashionItem temp;
			for(int i = 0, size = allFashions.size(); i < size; i++) {
				temp = allFashions.get(i);
				switch(temp.getType()) {
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
			if(suitCount > 0) {
				fighting += _fashionFightingCfgDAO.getCfgById(String.valueOf(suitCount)).getFightingOfSuit();
			}
			if(wingCount > 0) {
				fighting += _fashionFightingCfgDAO.getCfgById(String.valueOf(wingCount)).getFightingOfWing();
			}
			if(petCount > 0) {
				fighting += _fashionFightingCfgDAO.getCfgById(String.valueOf(suitCount)).getFightingOfPet();
			}
		}
		return fighting;
	}

}
