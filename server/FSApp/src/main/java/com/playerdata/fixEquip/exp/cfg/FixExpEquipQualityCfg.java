package com.playerdata.fixEquip.exp.cfg;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.fixEquip.FixEquipCostType;


public class FixExpEquipQualityCfg {

	private String id;

	private String planId;	
	
	private int quality;	

	private int levelNeed;
	
	private FixEquipCostType costType;
	
	private int costCount;	
	
	//modelAId:count;modelBId:count
	private String itemsNeedStr;
	
	private Map<Integer,Integer> itemsNeed = new HashMap<Integer,Integer>();
	
	private String attrData;
	
	private String precentAttrData;
	
	
	public String getId() {
		return id;
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

	public String getPlanId() {
		return planId;
	}

	public String getAttrData() {
		return attrData;
	}

	public String getPrecentAttrData() {
		return precentAttrData;
	}

	

	
	
	
	
}
