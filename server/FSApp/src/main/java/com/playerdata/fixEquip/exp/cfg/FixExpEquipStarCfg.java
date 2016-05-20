package com.playerdata.fixEquip.exp.cfg;

import java.util.HashMap;
import java.util.Map;

import com.playerdata.fixEquip.FixEquipCostType;


public class FixExpEquipStarCfg {

	private String id;
	//所属活动配置id
	private String parentCfgId;	
	
	private int star;	

	private int levelNeed;
	
	private FixEquipCostType costType;
	
	private int upCost;
	
	private int downCost;
	
	
	//modelAId:count;modelBId:count
	private String itemsNeedStr;
	
	private Map<Integer,Integer> itemsNeed = new HashMap<Integer,Integer>();
	
	public String getId() {
		return id;
	}

	public String getParentCfgId() {
		return parentCfgId;
	}


	public int getStar() {
		return star;
	}

	public int getLevelNeed() {
		return levelNeed;
	}

	public FixEquipCostType getCostType() {
		return costType;
	}

	public int getUpCost() {
		return upCost;
	}

	public int getDownCost() {
		return downCost;
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
