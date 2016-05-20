package com.playerdata.fixEquip.exp.cfg;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.fixEquip.FixEquipCostType;


public class FixExpEquipQualityCfg {

	private String id;
	//所属活动配置id
	private String parentCfgId;	
	
	private int quality;	

	private int levelNeed;
	
	private FixEquipCostType costType;
	
	private int costCount;
	
	
	//modelAId:count;modelBId:count
	private String itemsNeedStr;
	
	private Map<Integer,Integer> itemsNeed = new HashMap<Integer,Integer>();
	
	public String getId() {
		return id;
	}

	public String getParentCfgId() {
		return parentCfgId;
	}

	public int getQuality() {
		return quality;
	}

	public int getLevelNeed() {
		return levelNeed;
	}

	public FixEquipCostType getCostType() {
		return costType;
	}

	public int getCostCount() {
		return costCount;
	}

	public String getItemsNeedStr() {
		return itemsNeedStr;
	}

	public Map<Integer, Integer> getItemsNeed() {
		return itemsNeed;
	}

	public void setItemsNeed(Map<Integer, Integer> itemsNeed) {
		this.itemsNeed = itemsNeed;
	}

	

	
	
	
	
}
