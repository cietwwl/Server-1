package com.playerdata.fixEquip.norm;

import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.FixEquipResult;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipLevelCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipStarCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItemHolder;


public class FixNormEquipMgr {
	
	private static FixNormEquipMgr instance = new FixNormEquipMgr();
	
	
	public static FixNormEquipMgr getInstance(){
		return instance;
	}
	
	public void synDateTypeData(Player player){
		FixExpEquipDataItemHolder.getInstance().synAllData(player);
	}


	public FixEquipResult levelUp(Player player, String ownerId, String cfgId){
		FixExpEquipDataItemHolder dataHolder = FixExpEquipDataItemHolder.getInstance();
		
		FixExpEquipDataItem dataItem = dataHolder.getItem(ownerId, cfgId);
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
					dataHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}
	public FixEquipResult qualityUp(Player player, String ownerId, String cfgId){
		FixExpEquipDataItemHolder dataHolder = FixExpEquipDataItemHolder.getInstance();
		
		FixExpEquipDataItem dataItem = dataHolder.getItem(ownerId, cfgId);
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
					dataHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}
	
	public FixEquipResult starUp(Player player, String ownerId, String cfgId){
		FixExpEquipDataItemHolder dataHolder = FixExpEquipDataItemHolder.getInstance();
		
		FixExpEquipDataItem dataItem = dataHolder.getItem(ownerId, cfgId);
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
					dataHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}

	
	public FixEquipResult starDown(Player player, String ownerId, String cfgId){
		FixExpEquipDataItemHolder dataHolder = FixExpEquipDataItemHolder.getInstance();
		
		FixExpEquipDataItem dataItem = dataHolder.getItem(ownerId, cfgId);
		FixEquipResult result = FixEquipResult.newInstance(false);

		return result;
	}
	
	
	

}
