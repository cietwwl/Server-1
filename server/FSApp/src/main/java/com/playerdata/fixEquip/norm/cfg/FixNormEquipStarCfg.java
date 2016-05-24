package com.playerdata.fixEquip.norm.cfg;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.fixEquip.FixEquipCostType;


public class FixNormEquipStarCfg {

	private String id;
	//所属活动配置id
	private String planId;	
	
	private int star;	

	private int levelNeed;
	
	private FixEquipCostType upCostType;
	
	private int upCount;
	
	private FixEquipCostType downCostType;
	
	private int downCount;
	
	
	//modelAId:count;modelBId:count
	private String itemsNeedStr;
	
	private Map<Integer,Integer> itemsNeed = new HashMap<Integer,Integer>();
	
	public String getId() {
		return id;
	}


	public String getPlanId() {
		return planId;
	}


	public int getStar() {
		return star;
	}

	public int getLevelNeed() {
		return levelNeed;
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


	public FixEquipCostType getUpCostType() {
		return upCostType;
	}


	public int getUpCount() {
		return upCount;
	}


	public FixEquipCostType getDownCostType() {
		return downCostType;
	}


	public int getDownCount() {
		return downCount;
	}


	
	

	
	
	
	
}
