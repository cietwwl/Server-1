package com.playerdata.fixEquip.norm;

import com.common.Action;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.FixEquipResult;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipLevelCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfgDAO;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItemHolder;


public class FixNormEquipMgr {
	
	private FixNormEquipDataItemHolder fixNormEquipDataItemHolder;

	public boolean init(Hero pOwner) {
		fixNormEquipDataItemHolder = new FixNormEquipDataItemHolder();
		return true;
	}

	public void regChangeCallBack(Action callBack) {
		fixNormEquipDataItemHolder.regChangeCallBack(callBack);
	}
	
	public void synAllData(Player player, Hero hero){
		fixNormEquipDataItemHolder.synAllData(player, hero);
	}
	

	public FixEquipResult levelUp(Player player, String ownerId, String cfgId){
		
		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, cfgId);
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{			
			int nextLevel = dataItem.getLevel()+1;
			FixNormEquipLevelCfg levelCfg = FixNormEquipLevelCfgDAO.getInstance().getByParentCfgIdAndLevel(dataItem.getCfgId(), nextLevel);
			if(levelCfg == null){
				result.setReason("装备等级不存在。");
			}else{
				result = FixEquipHelper.takeCost(player, levelCfg.getCostType(), levelCfg.getCostCount());
				if(result.isSuccess()){
					dataItem.setLevel(nextLevel);
					fixNormEquipDataItemHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}
	public FixEquipResult qualityUp(Player player, String ownerId, String cfgId){
		
		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, cfgId);
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{			
			int nextQuality = dataItem.getQuality()+1;
			FixNormEquipQualityCfg levelCfg = FixNormEquipQualityCfgDAO.getInstance().getByParentCfgIdAndQuality(dataItem.getCfgId(), nextQuality);
			if(levelCfg == null){
				result.setReason("装备品格不存在。");
			}else{
				result = FixEquipHelper.takeCost(player, levelCfg.getCostType(), levelCfg.getCostCount());
				if(result.isSuccess()){
					dataItem.setQuality(nextQuality);
					fixNormEquipDataItemHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}
	
	public FixEquipResult starUp(Player player, String ownerId, String cfgId){
		
		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, cfgId);
		FixEquipResult result = FixEquipResult.newInstance(false);
		
		//未激活
		if(dataItem == null){
			result.setReason("装备不存在。");			
		}else{			
			int nextStar = dataItem.getStar()+1;
			FixNormEquipStarCfg levelCfg = FixNormEquipStarCfgDAO.getInstance().getByParentCfgIdAndStar(dataItem.getCfgId(), nextStar);
			if(levelCfg == null){
				result.setReason("装备星级不存在。");
			}else{
				result = FixEquipHelper.takeCost(player, levelCfg.getCostType(), levelCfg.getCostCount());
				if(result.isSuccess()){
					dataItem.setStar(nextStar);
					fixNormEquipDataItemHolder.updateItem(player, dataItem);
				}
			}
			
		}
		
		
		return result;
	}

	
	public FixEquipResult starDown(Player player, String ownerId, String cfgId){
		
		FixNormEquipDataItem dataItem = fixNormEquipDataItemHolder.getItem(ownerId, cfgId);
		FixEquipResult result = FixEquipResult.newInstance(false);

		return result;
	}
	
	
	

}
