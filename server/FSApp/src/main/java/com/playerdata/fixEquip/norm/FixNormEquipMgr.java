package com.playerdata.fixEquip.norm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.common.Action;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.FixEquipResult;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCostCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCostCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfgDAO;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItemHolder;


public class FixNormEquipMgr {
	
	private FixNormEquipDataItemHolder fixNormEquipDataItemHolder = new FixNormEquipDataItemHolder();

	public boolean newHeroInit(Player player, String ownerId, int modelId ){
		List<FixNormEquipDataItem> equipItemList = new ArrayList<FixNormEquipDataItem>();
	
		RoleFixEquipCfg roleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(modelId));		
		
		for (String cfgId : roleFixEquipCfg.getNormCfgIdList()) {
			
			String id = FixEquipHelper.getNormItemId(ownerId, cfgId);
			
			FixNormEquipDataItem  FixNormEquipDataItem = new FixNormEquipDataItem();
			FixNormEquipDataItem.setId( id );
			FixNormEquipDataItem.setCfgId(cfgId);
			FixNormEquipDataItem.setOwnerId(ownerId);
			FixNormEquipDataItem.setQuality(0);
			FixNormEquipDataItem.setLevel(1);
			FixNormEquipDataItem.setStar(0);
			equipItemList.add(FixNormEquipDataItem);
			
		}
		
		return fixNormEquipDataItemHolder.initItems(player, ownerId, equipItemList);
		
		
	}
	

	public void regChangeCallBack(Action callBack) {
		fixNormEquipDataItemHolder.regChangeCallBack(callBack);
	}
	
	public void synAllData(Player player, Hero hero){
		fixNormEquipDataItemHolder.synAllData(player, hero);
	}
	

	public FixEquipResult levelUp(Player player, String ownerId, String itemId){
		
		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);
		FixEquipResult result = checkLevel(player, ownerId, dataItem);
		if(result.isSuccess()){
			int nextLevel = dataItem.getLevel()+1;
			result = doLevelUp(player, dataItem, nextLevel);
		}	
		
		return result;
	}
	
	private FixEquipResult checkLevel(Player player, String ownerId, FixNormEquipDataItem dataItem){
		FixEquipResult result = FixEquipResult.newInstance(false);
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{
			int nextLevel = dataItem.getLevel()+1;
			Hero targetHero = player.getHeroMgr().getHeroById(ownerId);
			
			if(targetHero.getLevel() <= nextLevel){
				result.setReason("装备等级不能超过英雄等级。");	
			}else{
				result.setSuccess(true);
			}
			
		}
		return result;
	}


	private FixEquipResult doLevelUp(Player player,FixNormEquipDataItem dataItem, int nextLevel) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		FixNormEquipLevelCostCfg levelCostCfg = FixNormEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getCfgId(), nextLevel);
		if(levelCostCfg == null){
			result.setReason("装备已经达到最高级。");
		}else{
			result = FixEquipHelper.takeCost(player, levelCostCfg.getCostType(), levelCostCfg.getCostCount());
			if(result.isSuccess()){
				dataItem.setLevel(nextLevel);
				fixNormEquipDataItemHolder.updateItem(player, dataItem);
			}
		}
		return result;
	}
	public FixEquipResult qualityUp(Player player, String ownerId, String itemId){
		
		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);
		
		FixEquipResult result = checkQuality(player, ownerId, dataItem);
		if(result.isSuccess()){
			int nextQuality = dataItem.getQuality()+1;
			result = doQualityUp(player, dataItem, nextQuality);
		}
		
		return result;
	}

	private FixEquipResult checkQuality(Player player, String ownerId, FixNormEquipDataItem dataItem){
		
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{
			int curlevel = dataItem.getLevel();
			int nextQuality = dataItem.getQuality()+1;
			
			FixNormEquipQualityCfg nextQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getCfgId(), nextQuality);
			if(nextQualityCfg == null){
				result.setReason("装备已经达到最品质。");
			}else if(curlevel < nextQualityCfg.getLevelNeed() ){
				result.setReason("装备等级不够。");	
			}else{
				result.setSuccess(true);
			}
		}
		return result;
	}

	private FixEquipResult doQualityUp(Player player,FixNormEquipDataItem dataItem, int nextQuality) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		FixNormEquipQualityCfg qualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getCfgId(), nextQuality);
		result = FixEquipHelper.takeCost(player, qualityCfg.getCostType(), qualityCfg.getCostCount());
		if(result.isSuccess()){
			Map<Integer, Integer> itemsNeed = qualityCfg.getItemsNeed();
			result = FixEquipHelper.takeItemCost(player, itemsNeed);
		}		
		
		if(result.isSuccess()){
			dataItem.setQuality(nextQuality);
			fixNormEquipDataItemHolder.updateItem(player, dataItem);
		}
		return result;
		
	}
	
	public FixEquipResult starUp(Player player, String ownerId, String itemId){
		
		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);
		
		FixEquipResult result = checkStarUp(player, ownerId, dataItem);		
		if(result.isSuccess()){
			result = doStarUp(player, dataItem);
		}
		
		return result;
	}

	private FixEquipResult checkStarUp(Player player, String ownerId, FixNormEquipDataItem dataItem){
		
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{					
			result.setSuccess(true);
		}
		return result;
	}

	private FixEquipResult doStarUp(Player player,FixNormEquipDataItem dataItem ) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		int nextStar = dataItem.getStar()+1;
		FixNormEquipStarCfg nextStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getCfgId(), nextStar);
		if(nextStarCfg == null){
			result.setReason("装备已达最高星级。");
		}else{
			result = FixEquipHelper.takeCost(player, nextStarCfg.getUpCostType(), nextStarCfg.getUpCount());
			
			if(result.isSuccess()){
				Map<Integer, Integer> itemsNeed = nextStarCfg.getItemsNeed();
				result = FixEquipHelper.takeItemCost(player, itemsNeed);
			}	
			
			if(result.isSuccess()){
				dataItem.setStar(nextStar);
				fixNormEquipDataItemHolder.updateItem(player, dataItem);
			}
		}
		return result;
	}

	
	public FixEquipResult starDown(Player player, String ownerId, String itemId){
		
		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, itemId);
		
		FixEquipResult result = checkStarDown(player, ownerId, dataItem);
		if(result.isSuccess()){
			int curStar = dataItem.getStar();
			int nextStar = curStar -1;

			FixNormEquipStarCfg curStarCfg = FixNormEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getCfgId(), curStar);
			result = FixEquipHelper.takeCost(player, curStarCfg.getDownCostType(), curStarCfg.getDownCount());
			
			if(result.isSuccess()){
				dataItem.setStar(nextStar);
				fixNormEquipDataItemHolder.updateItem(player, dataItem);
				
				Map<Integer, Integer> itemsNeed = curStarCfg.getItemsNeed();
				result = FixEquipHelper.takeItemCost(player, itemsNeed);
			}	
		}

		return result;
	}
	
	private FixEquipResult checkStarDown(Player player, String ownerId, FixNormEquipDataItem dataItem){
		
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{
			int nextStar = dataItem.getStar()-1;
			if(nextStar < 0){
				result.setReason("装备已是最低等级。");	
			}else{
				result.setSuccess(true);
			}			
		}
		return result;
	}
	

}
