package com.playerdata.activity.retrieve.cfg;

import java.util.HashMap;

import com.rwbase.common.enu.eSpecialItemId;

public class RewardBackCfg {
	
	private String id ;
	
	private int type;//用于在附表查找对应的奖励消耗列；
	
	private String normalRewards;
	
	private HashMap<Integer, Integer> normalRewardsMap = new HashMap<Integer, Integer>();
	
	private int normalCostType;	
	
	private String normalCost;
	
	private HashMap<Integer, Integer> normalCostMap = new HashMap<Integer, Integer>();
	
	private String perfectRewards;
	
	private HashMap<Integer, Integer> perfectRewardsMap = new HashMap<Integer, Integer>();
	
	private int perfectCostType;
	
	private eSpecialItemId ePerfectCostType;
	
	private String perfectCost;
	
	private HashMap<Integer, Integer> perfectCostMap = new HashMap<Integer, Integer>();

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

	public HashMap<Integer, Integer> getNormalRewardsMap() {
		return normalRewardsMap;
	}

	public void setNormalRewardsMap(HashMap<Integer, Integer> normalRewardsMap) {
		this.normalRewardsMap = normalRewardsMap;
	}

	public int getNormalCostType() {
		return normalCostType;
	}

	public void setNormalCostType(int normalCostType) {
		this.normalCostType = normalCostType;
	}

	public String getNormalCost() {
		return normalCost;
	}

	public void setNormalCost(String normalCost) {
		this.normalCost = normalCost;
	}

	public HashMap<Integer, Integer> getNormalCostMap() {
		return normalCostMap;
	}

	public void setNormalCostMap(HashMap<Integer, Integer> normalCostMap) {
		this.normalCostMap = normalCostMap;
	}

	public String getPerfectRewards() {
		return perfectRewards;
	}

	public void setPerfectRewards(String perfectRewards) {
		this.perfectRewards = perfectRewards;
	}

	public HashMap<Integer, Integer> getPerfectRewardsMap() {
		return perfectRewardsMap;
	}

	public void setPerfectRewardsMap(HashMap<Integer, Integer> perfectRewardsMap) {
		this.perfectRewardsMap = perfectRewardsMap;
	}

	public int getPerfectCostType() {
		return perfectCostType;
	}

	public void setPerfectCostType(int perfectCostType) {
		this.perfectCostType = perfectCostType;
	}

	public eSpecialItemId getePerfectCostType() {
		return ePerfectCostType;
	}

	public void setePerfectCostType(eSpecialItemId ePerfectCostType) {
		this.ePerfectCostType = ePerfectCostType;
	}

	public String getPerfectCost() {
		return perfectCost;
	}

	public void setPerfectCost(String perfectCost) {
		this.perfectCost = perfectCost;
	}

	public HashMap<Integer, Integer> getPerfectCostMap() {
		return perfectCostMap;
	}

	public void setPerfectCostMap(HashMap<Integer, Integer> perfectCostMap) {
		this.perfectCostMap = perfectCostMap;
	}
	
	
	
}
