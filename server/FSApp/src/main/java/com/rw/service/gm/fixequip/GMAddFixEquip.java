package com.rw.service.gm.fixequip;

import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfgDAO;

public class GMAddFixEquip {

	
	
	public static void addStarUp(Player player){
		player.getItemBagMgr().addItem(806553, 9999);//基础升星材料，先加上
		List<FixNormEquipStarCfg> cfgList = FixNormEquipStarCfgDAO.getInstance().getByStar(1);
		for(FixNormEquipStarCfg cfg : cfgList){
			for(Map.Entry<Integer, Integer> entry: cfg.getItemsNeed().entrySet()){
				if(entry.getKey() == 806553){
					continue;
				}
				player.getItemBagMgr().addItem(entry.getKey(), 9999);//各英雄升星特色材料
			}	
		}
		
	}

	public static void addexp(Player player) {
		player.getItemBagMgr().addItem(806505, 9999);//护符经验
		player.getItemBagMgr().addItem(806510, 9999);//魂器经验
		
	}

	public static void addqualityUp(Player player) {
		player.getItemBagMgr().addItem(806551, 9999);//护符进阶
		player.getItemBagMgr().addItem(806552, 9999);//魂器进阶
		List<FixNormEquipQualityCfg> cfgList = FixNormEquipQualityCfgDAO.getInstance().getByPlanId(10001);
		for(FixNormEquipQualityCfg cfg : cfgList){
			for(Map.Entry<Integer, Integer> entry: cfg.getItemsNeed().entrySet()){
				player.getItemBagMgr().addItem(entry.getKey(), 9999);//普通材料进阶
			}			
		}
	}
}
