package com.playerdata.fixEquip.exp;

import java.util.ArrayList;
import java.util.List;

import com.common.Action;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.FixEquipResult;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItemHolder;


public class FixExpEquipMgr {
	
	private FixExpEquipDataItemHolder fixExpEquipDataItemHolder;

	public boolean newHeroInit(Player player, String ownerId, int modelId ){
		List<FixExpEquipDataItem> equipItemList = new ArrayList<FixExpEquipDataItem>();
		List<FixExpEquipCfg> equipCfgList = FixExpEquipCfgDAO.getInstance().getByHeroModelId(modelId);
		for (FixExpEquipCfg fixExpEquipCfg : equipCfgList) {
			
			String cfgId = fixExpEquipCfg.getId();
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
	

	public FixEquipResult levelUp(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{			
			int nextLevel = dataItem.getLevel()+1;
			FixExpEquipLevelCfg levelCfg = FixExpEquipLevelCfgDAO.getInstance().getByParentCfgIdAndLevel(dataItem.getCfgId(), nextLevel);
			if(levelCfg == null){
				result.setReason("装备等级不存在。");
			}else{
				result = FixEquipHelper.takeCost(player, levelCfg.getCostType(), levelCfg.getCostCount());
				if(result.isSuccess()){
					dataItem.setLevel(nextLevel);
					fixExpEquipDataItemHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}
	public FixEquipResult qualityUp(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{			
			int nextQuality = dataItem.getQuality()+1;
			FixExpEquipQualityCfg levelCfg = FixExpEquipQualityCfgDAO.getInstance().getByParentCfgIdAndQuality(dataItem.getCfgId(), nextQuality);
			if(levelCfg == null){
				result.setReason("装备品格不存在。");
			}else{
				result = FixEquipHelper.takeCost(player, levelCfg.getCostType(), levelCfg.getCostCount());
				if(result.isSuccess()){
					dataItem.setQuality(nextQuality);
					fixExpEquipDataItemHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}
	
	public FixEquipResult starUp(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{			
			int nextStar = dataItem.getStar()+1;
			FixExpEquipStarCfg levelCfg = FixExpEquipStarCfgDAO.getInstance().getByParentCfgIdAndStar(dataItem.getCfgId(), nextStar);
			if(levelCfg == null){
				result.setReason("装备星级不存在。");
			}else{
				result = FixEquipHelper.takeCost(player, levelCfg.getCostType(), levelCfg.getCostCount());
				if(result.isSuccess()){
					dataItem.setStar(nextStar);
					fixExpEquipDataItemHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}

	
	public FixEquipResult starDown(Player player, String ownerId, String itemId){
		
		FixExpEquipDataItem dataItem = fixExpEquipDataItemHolder.getItem(ownerId, itemId);
		FixEquipResult result = FixEquipResult.newInstance(false);

		return result;
	}
	
	
	

}
