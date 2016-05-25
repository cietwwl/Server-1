package com.playerdata.fixEquip.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.Action;
import com.playerdata.Hero;
import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.FixEquipResult;
import com.playerdata.fixEquip.cfg.FixEquipCfg;
import com.playerdata.fixEquip.cfg.FixEquipCfgDAO;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCostCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCostCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItemHolder;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.enu.eConsumeTypeDef;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwproto.FixEquipProto.ExpLevelUpReqParams;
import com.rwproto.FixEquipProto.SelectItem;


public class FixExpEquipMgr {
	
	private FixExpEquipDataItemHolder fixExpEquipDataItemHolder = new FixExpEquipDataItemHolder();

	public boolean newHeroInit(Player player, String ownerId, int modelId ){
		List<FixExpEquipDataItem> equipItemList = new ArrayList<FixExpEquipDataItem>();
		
		RoleFixEquipCfg roleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(modelId));
		
		
		for (String cfgId : roleFixEquipCfg.getExpCfgIdList()) {
			String id = FixEquipHelper.getExpItemId(ownerId, cfgId);
			
			FixExpEquipDataItem  FixExpEquipDataItem = new FixExpEquipDataItem();
			FixExpEquipDataItem.setId( id );
			FixExpEquipDataItem.setCfgId(cfgId);
			FixExpEquipDataItem.setOwnerId(ownerId);
			FixExpEquipDataItem.setQuality(0);
			FixExpEquipDataItem.setLevel(0);
			FixExpEquipDataItem.setStar(0);
			equipItemList.add(FixExpEquipDataItem);
			
		}
		
		return fixExpEquipDataItemHolder.initItems(player, ownerId, equipItemList);
	}
	

	public void regChangeCallBack(Action callBack) {
		fixExpEquipDataItemHolder.regChangeCallBack(callBack);
	}
	
	public void synAllData(Player player, Hero hero){
		fixExpEquipDataItemHolder.synAllData(player, hero);
	}
	

	public List<AttributeItem> toAttrItems(String ownerId){
		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);
		HashMap<Integer, AttributeItem> attrMap = new HashMap<Integer, AttributeItem>();
		for (FixExpEquipDataItem fixExpEquipDataItem : itemList) {
			String cfgId = fixExpEquipDataItem.getCfgId();
			FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(cfgId);
			

			FixExpEquipLevelCfg curLevelCfg = FixExpEquipLevelCfgDAO.getInstance().getByPlanIdAndLevel(equipCfg.getLevelPlanId(), fixExpEquipDataItem.getLevel());
			
			AttributeUtils.calcAttribute(curLevelCfg.getAttrDataMap(),  curLevelCfg.getPrecentAttrDataMap(), attrMap );
			
			FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(equipCfg.getQualityPlanId(), fixExpEquipDataItem.getQuality());
			AttributeUtils.calcAttribute(curQualityCfg.getAttrDataMap(),  curQualityCfg.getPrecentAttrDataMap(), attrMap );
			
			FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(equipCfg.getStarPlanId(), fixExpEquipDataItem.getStar());
			AttributeUtils.calcAttribute(curStarCfg.getAttrDataMap(),  curStarCfg.getPrecentAttrDataMap(), attrMap );
			
		}
		List<AttributeItem> attrItemList = new ArrayList<AttributeItem>(attrMap.values());
		return attrItemList;
	}
	
	public FixEquipResult levelUp(Player player, String ownerId, String itemId, ExpLevelUpReqParams reqParams){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		FixEquipResult result = checkLevel(player, ownerId, dataItem);
		if(result.isSuccess()){			
			result = doLevelUp(player, dataItem, reqParams);
		}	
		
		return result;
	}
	
	private FixEquipResult checkLevel(Player player, String ownerId, FixExpEquipDataItem dataItem){
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


	private FixEquipResult doLevelUp(Player player,FixExpEquipDataItem dataItem, ExpLevelUpReqParams reqParams) {
		
		List<SelectItem> selectItemList = reqParams.getSelectItemList();
		int totalExp = selectItems2Exp(selectItemList);
		
		FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
		int totalCost = totalExp * equipCfg.getCostPerExp();
		
		FixEquipResult result = FixEquipHelper.takeCost(player, equipCfg.getExpCostType(), totalCost);
		if(result.isSuccess()){			
			Map<Integer,Integer> itemsSelected = new HashMap<Integer, Integer>();
			for (SelectItem selectItem : selectItemList) {
				int modelId = selectItem.getModelId();
				int count = selectItem.getCount();
				itemsSelected.put(modelId, count);
			}
			result = FixEquipHelper.takeItemCost(player, itemsSelected);
		}
		
		if(result.isSuccess()){
			iterateLevelUp(dataItem, totalExp);
			fixExpEquipDataItemHolder.updateItem(player, dataItem);
		}
		
		return result;
	}


	private void iterateLevelUp(FixExpEquipDataItem dataItem, int totalExp) {
		while(totalExp > 0 ){
			int curLevel = dataItem.getLevel();
			int nextLevel = curLevel+1;
			
			FixExpEquipLevelCostCfg curLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getCfgId(), curLevel);
			FixExpEquipLevelCostCfg nextLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getCfgId(), nextLevel);
			if(nextLevelCfg == null){//已经是最满级
				dataItem.setExp(curLevelCfg.getExpNeed());
				break;
			}else{
				
				if( totalExp+dataItem.getExp() >= curLevelCfg.getExpNeed()){
					dataItem.setLevel(nextLevel);
					dataItem.setExp(0);
					totalExp = totalExp - (curLevelCfg.getExpNeed() - dataItem.getExp());
				}else{
					dataItem.setExp( totalExp+dataItem.getExp() );
					totalExp = 0;
				}
			}
			
		}
	}


	private int selectItems2Exp(List<SelectItem> selectItemList) {
		int totalExp = 0;
		for (SelectItem selectItem : selectItemList) {
			int modelId = selectItem.getModelId();
			int count = selectItem.getCount();
			ConsumeCfg consumeCfg = ItemCfgHelper.getConsumeCfg(modelId);
			if(eConsumeTypeDef.Exp4FixEquip.ordinal() == consumeCfg.getConsumeType()){
				totalExp = totalExp + consumeCfg.getValue()*count;
			}
		}
		return totalExp;
	}
	public FixEquipResult qualityUp(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		
		FixEquipResult result = checkQuality(player, ownerId, dataItem);
		if(result.isSuccess()){
			int nextQuality = dataItem.getQuality()+1;
			result = doQualityUp(player, dataItem, nextQuality);
		}
		
		return result;
	}

	private FixEquipResult checkQuality(Player player, String ownerId, FixExpEquipDataItem dataItem){
		
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{
			int curlevel = dataItem.getLevel();
			int nextQuality = dataItem.getQuality()+1;
			
			FixExpEquipQualityCfg nextQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getCfgId(), nextQuality);
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

	private FixEquipResult doQualityUp(Player player,FixExpEquipDataItem dataItem, int nextQuality) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		FixExpEquipQualityCfg qualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getCfgId(), nextQuality);
		result = FixEquipHelper.takeCost(player, qualityCfg.getCostType(), qualityCfg.getCostCount());
		if(result.isSuccess()){
			Map<Integer, Integer> itemsNeed = qualityCfg.getItemsNeed();
			result = FixEquipHelper.takeItemCost(player, itemsNeed);
		}		
		
		if(result.isSuccess()){
			dataItem.setQuality(nextQuality);
			fixExpEquipDataItemHolder.updateItem(player, dataItem);
		}
		return result;
		
	}
	
	public FixEquipResult starUp(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		
		FixEquipResult result = checkStarUp(player, ownerId, dataItem);		
		if(result.isSuccess()){
			result = doStarUp(player, dataItem);
		}
		
		return result;
	}

	private FixEquipResult checkStarUp(Player player, String ownerId, FixExpEquipDataItem dataItem){
		
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{
			result.setSuccess(true);
		}
		return result;
	}

	private FixEquipResult doStarUp(Player player,FixExpEquipDataItem dataItem ) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		int nextStar = dataItem.getStar()+1;
		FixExpEquipStarCfg nextStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getCfgId(), nextStar);
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
				fixExpEquipDataItemHolder.updateItem(player, dataItem);
			}
		}
		return result;
	}

	
	public FixEquipResult starDown(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		
		FixEquipResult result = checkStarDown(player, ownerId, dataItem);
		if(result.isSuccess()){
			int curStar = dataItem.getStar();
			int nextStar = curStar -1;

			FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getCfgId(), curStar);
			result = FixEquipHelper.takeCost(player, curStarCfg.getDownCostType(), curStarCfg.getDownCount());
			
			if(result.isSuccess()){
				dataItem.setStar(nextStar);
				fixExpEquipDataItemHolder.updateItem(player, dataItem);
				
				Map<Integer, Integer> itemsNeed = curStarCfg.getItemsNeed();
				result = FixEquipHelper.takeItemCost(player, itemsNeed);
			}	
		}

		return result;
	}
	
	private FixEquipResult checkStarDown(Player player, String ownerId, FixExpEquipDataItem dataItem){
		
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
