package com.rw.service.gm.fixequip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipStarCfgDAO;
import com.rwbase.dao.copy.pojo.ItemInfo;

public class GMAddFixEquip {

	
	
	public static void addStarUp(Player player){
//		player.getItemBagMgr().addItem(806553, 99999);//基础升星材料，先加上
		List<ItemInfo> list = new ArrayList<ItemInfo>();
		list.add(new ItemInfo(806553, 99999));
		List<FixNormEquipStarCfg> cfgList = FixNormEquipStarCfgDAO.getInstance().getByStar(1);
		for(FixNormEquipStarCfg cfg : cfgList){
			for(Map.Entry<Integer, Integer> entry: cfg.getItemsNeed().entrySet()){
				if(entry.getKey() == 806553){
					continue;
				}
//				player.getItemBagMgr().addItem(entry.getKey(), 99999);//各英雄升星特色材料
				list.add(new ItemInfo(entry.getKey(), 99999));
			}	
		}
		player.getItemBagMgr().addItem(list);
		
	}

	public static void addexp(Player player) {
//		player.getItemBagMgr().addItem(806505, 99999);//护符经验
//		player.getItemBagMgr().addItem(806510, 99999);//魂器经验
		List<ItemInfo> list = new ArrayList<ItemInfo>();
		list.add(new ItemInfo(806505, 99999));
		list.add(new ItemInfo(806510, 99999));
		player.getItemBagMgr().addItem(list);
	}

	public static void addqualityUp(Player player) {
		List<ItemInfo> list = new ArrayList<ItemInfo>();
//		player.getItemBagMgr().addItem(806551, 99999);//护符进阶
//		player.getItemBagMgr().addItem(806552, 99999);//魂器进阶
		list.add(new ItemInfo(806551, 99999));
		list.add(new ItemInfo(806552, 99999));
		List<FixNormEquipQualityCfg> cfgList = FixNormEquipQualityCfgDAO.getInstance().getGmByPlanId(10001+"");
		for(FixNormEquipQualityCfg cfg : cfgList){
			for(Map.Entry<Integer, Integer> entry: cfg.getItemsNeed().entrySet()){
//				player.getItemBagMgr().addItem(entry.getKey(), 99999);//普通材料进阶
				list.add(new ItemInfo(entry.getKey(), 99999));
			}			
		}
		player.getItemBagMgr().addItem(list);
	}
}
