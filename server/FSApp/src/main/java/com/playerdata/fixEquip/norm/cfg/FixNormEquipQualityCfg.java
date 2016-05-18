package com.playerdata.fixEquip.norm.cfg;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.fixEquip.FixEquipCostType;


public class FixNormEquipQualityCfg {

	private String id;
	//所属活动配置id
	private String parentCfgId;	
	
	private int quality;	

	private int levelNeed;
	
	private FixEquipCostType costType;
	
	private int costCount;
	
	
	//modelAId:count;modelBId:count
	private String itemsNeedStr;
	
	private Map<String,Integer> itemsNeed = new HashMap<String,Integer>();
	
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

	public Map<String, Integer> getItemsNeed() {
		return itemsNeed;
	}

	public void setItemsNeed(Map<String, Integer> itemsNeed) {
		this.itemsNeed = itemsNeed;
	}

	

	
	
	
	
}
