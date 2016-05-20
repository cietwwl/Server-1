package com.playerdata.fixEquip.exp.cfg;

import com.playerdata.fixEquip.FixEquipCostType;


public class FixExpEquipLevelCfg {

	private String id;
	//所属活动配置id
	private String parentCfgId;	
	private int level;
	//所需经验
	private int expNeed;
	
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

	public int getExpNeed() {
		return expNeed;
	}

	public FixEquipCostType getCostType() {
		return costType;
	}

	public int getCostCount() {
		return costCount;
	}
	
	
	

	
	
	
	
}
