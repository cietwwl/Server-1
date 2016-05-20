package com.playerdata.fixEquip;

import java.util.Map;

import com.playerdata.ItemBagMgr;
import com.playerdata.Player;

public class FixEquipHelper {

	
	public static String getExpItemId(String heroId, String cfgId){
		
		return heroId+"_"+cfgId;
	}
	public static String getNormItemId(String heroId, String cfgId){
		
		return heroId+"_"+cfgId;
	}
	
	public static FixEquipResult takeCost(Player player, FixEquipCostType costType, int count){
		
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		
		switch (costType) {
		case COIN:
			costCoin(player,count);
			break;
		case GOLD:
			costGold(player,count);			
			break;

		default:
			break;
		}
		
		
		return result;
		
		
	}
	public static FixEquipResult takeItemCost(Player player, Map<Integer,Integer> itemCostMap){		
		FixEquipResult result = FixEquipResult.newInstance(false);
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		
		if(costItemBag(itemBagMgr,itemCostMap)){
			result.setSuccess(true);
		}else{
			result.setReason("物品不足。");			
		}		
		return result;
		
	}
	private static boolean costItemBag(ItemBagMgr itemBagMgr,Map<Integer, Integer> itemCostMap) {
		boolean isItemEnough = true;
		for (int modelId : itemCostMap.keySet()) {
			int countInBag = itemBagMgr.getItemCountByModelId(modelId);
			if(itemCostMap.get(modelId) > countInBag){
				isItemEnough = false;
				break;
			}
			
		}
		boolean success = true;
		if(isItemEnough){			
			for (int modelId : itemCostMap.keySet()) {
				Integer need = itemCostMap.get(modelId);
				if(!itemBagMgr.addItem(modelId, -need)){
					success = false;
					break;
				}
				
			}
			
		}
		return success;
	}
	private static void costGold(Player player, int count) {
		// TODO Auto-generated method stub
		
	}
	private static void costCoin(Player player, int count) {
		// TODO Auto-generated method stub
		
	}
	
}
