package com.bm.worldBoss.service;

import com.playerdata.Player;
import com.rwbase.common.enu.eSpecialItemId;

public class WBHelper {

	
	
	
	public static WBResult checkCost(Player player, eSpecialItemId costType, int count) {

		WBResult result = WBResult.newInstance(false);

		switch (costType) {
		case Coin:
			if (checkCoin(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("金币不足");
			}
			break;
		case Gold:
			if (checkGold(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("钻石不足");
			}
			break;

		default:
			break;
		}
		return result;
	}

	public static WBResult takeCost(Player player, eSpecialItemId costType, int count) {

		WBResult result = WBResult.newInstance(false);

		switch (costType) {
		case Coin:
			if (costCoin(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("金币不足");
			}
			break;
		case Gold:
			if (costGold(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("钻石不足");
			}
			break;

		default:
			break;
		}
		return result;
	}
	
	
	public static boolean addCoin(Player player, int count) {
		if(count <0){
			return false;
		}
		
		int resultCode = player.getUserGameDataMgr().addCoin(count);
		return resultCode == 0;
	}

	private static boolean costGold(Player player, int count) {
		int resultCode = player.getUserGameDataMgr().addGold(-count);
		return resultCode == 0;
	}

	private static boolean costCoin(Player player, int count) {
		int resultCode = player.getUserGameDataMgr().addCoin(-count);
		return resultCode == 0;
	}

	private static boolean checkGold(Player player, int count) {

		return player.getUserGameDataMgr().isGoldEngough(-count);

	}

	private static boolean checkCoin(Player player, int count) {
		return player.getUserGameDataMgr().isCoinEnough(-count);
	}
}
