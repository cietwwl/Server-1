package com.playerdata.activity.retrieve.cfg;

import java.util.HashMap;

import com.rwbase.common.enu.eSpecialItemId;

public class RewardBackCfg {
	
	private String id ;
	
	private int type;//用于在附表查找对应的奖励消耗列；
	
	private String normalRewards;
	
//	private HashMap<Integer, Integer> normalRewardsMap = new HashMap<Integer, Integer>();
	
	private int normalCostType;	
	
	private int normalCost;	
	
	private String normalCost2;
	
	private HashMap<Integer, Integer> normalCost2Map = new HashMap<Integer, Integer>();
	
	private String perfectRewards;
	
//	private HashMap<Integer, Integer> perfectRewardsMap = new HashMap<Integer, Integer>();
	
	private int perfectCostType;	
	
	private int perfectCost;
	
	private String perfectCost2;	
	
	private HashMap<Integer, Integer> perfectCost2Map = new HashMap<Integer, Integer>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNormalRewards() {
		return normalRewards;
	}

	public void setNormalRewards(String normalRewards) {
		this.normalRewards = normalRewards;
	}

//	public HashMap<Integer, Integer> getNormalRewardsMap() {
//		return normalRewardsMap;
//	}
//
//	public void setNormalRewardsMap(HashMap<Integer, Integer> normalRewardsMap) {
//		this.normalRewardsMap = normalRewardsMap;
//	}

	public int getNormalCostType() {
		return normalCostType;
	}

	public void setNormalCostType(int normalCostType) {
		this.normalCostType = normalCostType;
	}

	public String getPerfectRewards() {
		return perfectRewards;
	}

	public void setPerfectRewards(String perfectRewards) {
		this.perfectRewards = perfectRewards;
	}

//	public HashMap<Integer, Integer> getPerfectRewardsMap() {
//		return perfectRewardsMap;
//	}
//
//	public void setPerfectRewardsMap(HashMap<Integer, Integer> perfectRewardsMap) {
//		this.perfectRewardsMap = perfectRewardsMap;
//	}

	public int getPerfectCostType() {
		return perfectCostType;
	}

	public void setPerfectCostType(int perfectCostType) {
		this.perfectCostType = perfectCostType;
	}

	public String getNormalCost2() {
		return normalCost2;
	}

	public void setNormalCost2(String normalCost2) {
		this.normalCost2 = normalCost2;
	}

	public HashMap<Integer, Integer> getNormalCost2Map() {
		return normalCost2Map;
	}

	public void setNormalCost2Map(HashMap<Integer, Integer> normalCost2Map) {
		this.normalCost2Map = normalCost2Map;
	}

	public String getPerfectCost2() {
		return perfectCost2;
	}

	public void setPerfectCost2(String perfectCost2) {
		this.perfectCost2 = perfectCost2;
	}

	public HashMap<Integer, Integer> getPerfectCost2Map() {
		return perfectCost2Map;
	}

	public void setPerfectCost2Map(HashMap<Integer, Integer> perfectCost2Map) {
		this.perfectCost2Map = perfectCost2Map;
	}

	public int getNormalCost() {
		return normalCost;
	}

	public void setNormalCost(int normalCost) {
		this.normalCost = normalCost;
	}

	public int getPerfectCost() {
		return perfectCost;
	}

	public void setPerfectCost(int perfectCost) {
		this.perfectCost = perfectCost;
	}	
	
	
}
