package com.playerdata.fixEquip.cfg;

import com.playerdata.fixEquip.FixEquipCostType;
import com.playerdata.fixEquip.FixEquipType;


public class FixEquipCfg {

	private String id;
	
	private String levelCostPlanId;
	
	private String levelPlanId;
	
	private String qualityPlanId;
	
	private String starPlanId;
	
	private FixEquipType fixEquipType;

	private FixEquipCostType expCostType = FixEquipCostType.COIN;
	
	private int costPerExp;
	
	private String name;
	
	private String awakenName;
	
	
	public String getId() {
		return id;
	}

	public String getLevelPlanId() {
		return levelPlanId;
	}	
	

	public String getLevelCostPlanId() {
		return levelCostPlanId;
	}

	public String getQualityPlanId() {
		return qualityPlanId;
	}

	public String getStarPlanId() {
		return starPlanId;
	}

	public FixEquipType getFixEquipType() {
		return fixEquipType;
	}

	public int getCostPerExp() {
		return costPerExp;
	}

	public FixEquipCostType getExpCostType() {
		return expCostType;
	}

	public String getName() {
		return name;
	}

	

	public String getAwakenName() {
		return awakenName;
	}
	
	
}
