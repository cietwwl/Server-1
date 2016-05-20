package com.playerdata.fixEquip.norm.cfg;

import com.playerdata.fixEquip.FixEquipCostType;


public class FixNormEquipLevelCfg {

	private String id;
	//所属活动配置id
	private String parentCfgId;	
	private int level;
	
	private FixEquipCostType costType;
	
	private int costCount;

	public String getId() {
		return id;
	}

	public String getParentCfgId() {
		return parentCfgId;
	}

	public int getLevel() {
		return level;
	}

	public FixEquipCostType getCostType() {
		return costType;
	}

	public int getCostCount() {
		return costCount;
	}
	
	
	

	
	
	
	
}
