package com.playerdata.fixEquip;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.ItemBagMgr;
import com.playerdata.Player;

public class FixEquipHelper {

	
	public static String getExpItemId(String heroId, String cfgId){
		
		return heroId+"_"+cfgId;
	}
	public static String getNormItemId(String heroId, String cfgId){
		
		return heroId+"_"+cfgId;
	}
	
	public static Map<Integer, Integer> parseNeedItems(String itemsNeedStr) {
		 Map<Integer, Integer> itemsNeed = new HashMap<Integer, Integer>();
		 if(StringUtils.isNotBlank(itemsNeedStr)){
			 //modelAId:count;modelBId:count
			 String[] itemArray = itemsNeedStr.split(";");
			 for (String itemTmp : itemArray) {
				 String[] split = itemTmp.split(":");
				 int modelId = Integer.valueOf(split[0]) ;
				 int count = Integer.valueOf(split[1]) ;
				 itemsNeed.put(modelId, count);
			 }
		 }
		return itemsNeed;
	}
	
	public static FixEquipResult takeCost(Player player, FixEquipCostType costType, int count){
		
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		
		switch (costType) {
		case COIN:
			if(costCoin(player,count)){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setReason("金币不足");				
			}
			break;
		case GOLD:
			if(costGold(player,count)){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setReason("钻石不足");				
			}		
			break;

		default:
			break;
		}
		return result;
	}
	private static boolean costGold(Player player, int count) {
		int resultCode = player.getUserGameDataMgr().addGold(-count);
		return resultCode == 0;
	}
	private static boolean costCoin(Player player, int count) {
		int resultCode = player.getUserGameDataMgr().addCoin(-count);
		return resultCode == 0;
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
	public static boolean turnBackItems(Player player, Map<Integer, Integer> itemCostMap) {
		
		boolean success = true;
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		for (Integer modelId : itemCostMap.keySet()) {
			Integer count = itemCostMap.get(modelId);
			itemBagMgr.addItem(modelId, count);
		}	
		
		return success;
	}

	
}
