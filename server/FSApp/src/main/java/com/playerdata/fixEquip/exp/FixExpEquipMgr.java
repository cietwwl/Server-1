package com.playerdata.fixEquip.exp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.rwbase.common.attribute.AttrCheckLoger;
import com.rwbase.common.attribute.AttributeItem;
import com.rwbase.common.attribute.AttributeUtils;
import com.rwbase.common.enu.eConsumeTypeDef;
import com.rwbase.dao.item.pojo.ConsumeCfg;
import com.rwproto.FixEquipProto.ExpLevelUpReqParams;
import com.rwproto.FixEquipProto.SelectItem;


public class FixExpEquipMgr {
	
	private FixExpEquipDataItemHolder fixExpEquipDataItemHolder = new FixExpEquipDataItemHolder();

	final private Comparator<FixExpEquipDataItem> comparator = new Comparator<FixExpEquipDataItem>() {
		
		@Override
		public int compare(FixExpEquipDataItem source,
				FixExpEquipDataItem target) {
			return source.getSlot() - target.getSlot();
		}
		
	};
	
	public boolean newHeroInit(Player player, String ownerId, int modelId ){
		List<FixExpEquipDataItem> equipItemList = new ArrayList<FixExpEquipDataItem>();
		
		RoleFixEquipCfg roleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(modelId));
		
		int slot = 4;
		for (String cfgId : roleFixEquipCfg.getExpCfgIdList()) {
			String id = FixEquipHelper.getExpItemId(ownerId, cfgId);
			
			FixExpEquipDataItem  FixExpEquipDataItem = new FixExpEquipDataItem();
			FixExpEquipDataItem.setId( id );
			FixExpEquipDataItem.setCfgId(cfgId);
			FixExpEquipDataItem.setOwnerId(ownerId);
			FixExpEquipDataItem.setQuality(1);
			FixExpEquipDataItem.setLevel(1);
			FixExpEquipDataItem.setStar(0);
			FixExpEquipDataItem.setSlot(slot);
			
			equipItemList.add(FixExpEquipDataItem);
			slot++;
			
			
		}
		
		Collections.sort(equipItemList, comparator);
		
		return fixExpEquipDataItemHolder.initItems(player, ownerId, equipItemList);
	}
	
	public boolean onCarrerChange( Player player ){
		
		Hero mainRoleHero = player.getMainRoleHero();
		int newModelId = mainRoleHero.getModelId();
		String ownerId = player.getUserId();
		
		List<FixExpEquipDataItem> itemList = fixExpEquipDataItemHolder.getItemList(ownerId);	
	
		RoleFixEquipCfg newRoleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(newModelId));		
		
		int slot = 0;
		for (String cfgId : newRoleFixEquipCfg.getExpCfgIdList()) {			
			FixExpEquipDataItem  item = getBySlot(itemList,slot);
			if(item!=null){
				item.setCfgId(cfgId);
			}
			slot++;
		}		
		
		fixExpEquipDataItemHolder.updateItemList(player, itemList);
		return true;
		
	}
	private FixExpEquipDataItem getBySlot(List<FixExpEquipDataItem> itemList, int slot) {
		FixExpEquipDataItem target = null;
		for (FixExpEquipDataItem fixNormEquipDataItem : itemList) {
			if(fixNormEquipDataItem.getSlot() == slot){
				target = fixNormEquipDataItem;
				break;
			}
		}
		return target;
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
		for (FixExpEquipDataItem itemTmp : itemList) {
			

			FixExpEquipLevelCfg curLevelCfg = FixExpEquipLevelCfgDAO.getInstance().getByPlanIdAndLevel(itemTmp.getLevelPlanId(), itemTmp.getLevel());
			
			AttributeUtils.calcAttribute(curLevelCfg.getAttrDataMap(),  curLevelCfg.getPrecentAttrDataMap(), attrMap );
			
			FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(itemTmp.getQualityPlanId(), itemTmp.getQuality());
			AttributeUtils.calcAttribute(curQualityCfg.getAttrDataMap(),  curQualityCfg.getPrecentAttrDataMap(), attrMap );
			
			FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(itemTmp.getStarPlanId(), itemTmp.getStar());
			AttributeUtils.calcAttribute(curStarCfg.getAttrDataMap(),  curStarCfg.getPrecentAttrDataMap(), attrMap );
			
			AttrCheckLoger.logAttr("经验神装", ownerId, attrMap);
			
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
			
			if(player.getLevel() < nextLevel){
				result.setReason("装备等级不能超过英雄等级。");	
			}else{				
				result.setSuccess(true);
			}
			
		}
		return result;
	}


	private FixEquipResult doLevelUp(Player player,FixExpEquipDataItem dataItem, ExpLevelUpReqParams reqParams) {
		
		List<SelectItem> selectItemList = reqParams.getSelectItemList();
		
		eConsumeTypeDef consumeType = null;
				
		if(dataItem.getSlot() == 4){
			consumeType = eConsumeTypeDef.Exp4FixEquip_4;
		}else if(dataItem.getSlot() == 5){
			consumeType = eConsumeTypeDef.Exp4FixEquip_5;
		}
				
		FixEquipResult result = FixEquipResult.newInstance(false);
		if(consumeType == null){
			result.setReason("所选经验道具和升级装备不匹配。");
		}else{
			
			int totalExp = selectItems2Exp(consumeType, selectItemList);
			int nextQualityNeedExp = getNextQualityNeedExp(dataItem);
			totalExp = totalExp < nextQualityNeedExp?totalExp:nextQualityNeedExp;		
			
			FixEquipCfg equipCfg = FixEquipCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
			int totalCost = totalExp * equipCfg.getCostPerExp();
			
			result = FixEquipHelper.takeCost(player, equipCfg.getExpCostType(), totalCost);
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
		}
				
		
		return result;
	}


	private int getNextQualityNeedExp(FixExpEquipDataItem dataItem) {
		FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), dataItem.getQuality());
		int curLevel = dataItem.getLevel();
		int nextQualityLevel = curQualityCfg.getLevelNeed();
		int expNeed = 0;
		for (int level = curLevel; level < nextQualityLevel; level++) {
			FixExpEquipLevelCostCfg levelCostCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), level);
		
			expNeed = expNeed + levelCostCfg.getExpNeed();
			
		}		
		return expNeed;
	}

	private void iterateLevelUp(FixExpEquipDataItem dataItem, int totalExp) {
		
		
		int curLevel = dataItem.getLevel();
		while(totalExp > 0 ){
			int nextLevel = curLevel+1;
			
			FixExpEquipLevelCostCfg curLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), curLevel);
			FixExpEquipLevelCostCfg nextLevelCfg = FixExpEquipLevelCostCfgDAO.getInstance().getByPlanIdAndLevel(dataItem.getLevelCostPlanId(), nextLevel);
			if(nextLevelCfg == null){//已经是最满级
				int exp = dataItem.getExp()+totalExp < curLevelCfg.getExpNeed()? dataItem.getExp()+ totalExp:curLevelCfg.getExpNeed();
				dataItem.setExp(exp);
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
			
			curLevel++;			
		}
	}


	private int selectItems2Exp(eConsumeTypeDef consumeType, List<SelectItem> selectItemList) {
		int totalExp = 0;
		for (SelectItem selectItem : selectItemList) {
			int modelId = selectItem.getModelId();
			int count = selectItem.getCount();
			ConsumeCfg consumeCfg = ItemCfgHelper.getConsumeCfg(modelId);
			if(consumeType.getOrder() == consumeCfg.getConsumeType() ){
				totalExp = totalExp + consumeCfg.getValue()*count;
			}
		}
		return totalExp;
	}
	public FixEquipResult qualityUp(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		
		FixEquipResult result = checkQuality(player, ownerId, dataItem);
		if(result.isSuccess()){
			result = doQualityUp(player, dataItem);
		}
		
		return result;
	}

	private FixEquipResult checkQuality(Player player, String ownerId, FixExpEquipDataItem dataItem){
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{
			int curlevel = dataItem.getLevel();
			int currentQuality = dataItem.getQuality();
			
			FixExpEquipQualityCfg nextQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), currentQuality+1);
			if(nextQualityCfg == null){
				result.setReason("装备已经达到最品质。");
			}else{
				
				FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), currentQuality);
				Map<Integer, Integer> itemsNeed = curQualityCfg.getItemsNeed();
				if(curlevel < curQualityCfg.getLevelNeed() ){
					result.setReason("装备等级不够。");	
				}else if(!FixEquipHelper.isItemEnough(player, itemsNeed)){
					result.setReason("进化材料不足.");	
				}else{
					result.setSuccess(true);
					
				}
			}
			
			
		}
		return result;
	}

	private FixEquipResult doQualityUp(Player player,FixExpEquipDataItem dataItem) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		int curQuality = dataItem.getQuality();
		FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), curQuality);
	
		result = FixEquipHelper.takeCost(player, curQualityCfg.getCostType(), curQualityCfg.getCostCount());
		if(result.isSuccess()){					
			Map<Integer, Integer> itemsNeed = curQualityCfg.getItemsNeed();
			result = FixEquipHelper.takeItemCost(player, itemsNeed);				
		}		
		
		if(result.isSuccess()){
			dataItem.setQuality(curQuality+1);
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
		}else {
			int curStar = dataItem.getStar();
			FixExpEquipStarCfg nextStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar+1);
			
			if(nextStarCfg == null){
				result.setReason("装备已达最高星级。");
			}else{
				FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);
				
				Map<Integer, Integer> itemsNeed = curStarCfg.getItemsNeed();
				if(!FixEquipHelper.isItemEnough(player, itemsNeed)){	
					result.setReason("觉醒材料不足");
				}else{					
					result.setSuccess(true);
				}
			}
		}
		return result;
	}

	private FixEquipResult doStarUp(Player player,FixExpEquipDataItem dataItem ) {
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		int curStar = dataItem.getStar();
		FixExpEquipStarCfg curStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), curStar);
		
		result = FixEquipHelper.takeCost(player, curStarCfg.getUpCostType(), curStarCfg.getUpCount());
		
		if(result.isSuccess()){
			Map<Integer, Integer> itemsNeed = curStarCfg.getItemsNeed();			
			result = FixEquipHelper.takeItemCost(player, itemsNeed);
		}	
		
		if(result.isSuccess()){
			dataItem.setStar(curStar+1);
			fixExpEquipDataItemHolder.updateItem(player, dataItem);
		}
		return result;
	}

	
	public FixEquipResult starDown(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		
		FixEquipResult result = checkStarDown(player, ownerId, dataItem);
		if(result.isSuccess()){
			int curStar = dataItem.getStar();
			int nextStar = curStar -1;

			FixExpEquipStarCfg nextStarCfg = FixExpEquipStarCfgDAO.getInstance().getByPlanIdAndStar(dataItem.getStarPlanId(), nextStar);
			result = FixEquipHelper.takeCost(player, nextStarCfg.getDownCostType(), nextStarCfg.getDownCount());
			
			if(result.isSuccess()){
				dataItem.setStar(nextStar);
				fixExpEquipDataItemHolder.updateItem(player, dataItem);
				
				Map<Integer, Integer> itemsNeed = nextStarCfg.getItemsNeed();
				result = FixEquipHelper.turnBackItemCost(player, itemsNeed);
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
