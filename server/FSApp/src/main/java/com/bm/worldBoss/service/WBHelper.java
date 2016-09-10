package com.bm.worldBoss.service;

import com.bm.worldBoss.cfg.WBCostType;
import com.playerdata.Player;

public class WBHelper {

	
	
	
	public static WBResult checkCost(Player player, WBCostType costType, int count) {

		WBResult result = WBResult.newInstance(false);

		switch (costType) {
		case COIN:
			if (checkCoin(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("金币不足");
			}
			break;
		case GOLD:
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

	public static WBResult takeCost(Player player, WBCostType costType, int count) {

		WBResult result = WBResult.newInstance(false);

		switch (costType) {
		case COIN:
			if (costCoin(player, count)) {
				result.setSuccess(true);
			} else {
				result.setSuccess(false);
				result.setReason("金币不足");
			}
			break;
		case GOLD:
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
