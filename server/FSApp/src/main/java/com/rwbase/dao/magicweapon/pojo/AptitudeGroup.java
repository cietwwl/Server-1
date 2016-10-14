package com.rwbase.dao.magicweapon.pojo;

import java.util.HashMap;

import com.common.Weight;

public class AptitudeGroup {
	public int minAptitude;
	public int maxAptitude;
	
	private Weight<MagicSmeltRateCfg> weight;
	public HashMap<MagicSmeltRateCfg, Integer> proMap = new HashMap<MagicSmeltRateCfg, Integer>();
	
	public MagicSmeltRateCfg getSmeltResult(){
		MagicSmeltRateCfg cfg = weight.getRanResult();
		return cfg;
	}

	public int getMinAptitude() {
		return minAptitude;
	}

	public void setMinAptitude(int minAptitude) {
		this.minAptitude = minAptitude;
	}

	public int getMaxAptitude() {
		return maxAptitude;
	}

	public void setMaxAptitude(int maxAptitude) {
		this.maxAptitude = maxAptitude;
	}

	public Weight<MagicSmeltRateCfg> getWeight() {
		return weight;
	}

	public void setWeight(Weight<MagicSmeltRateCfg> weight) {
		this.weight = weight;
	}

	public HashMap<MagicSmeltRateCfg, Integer> getProMap() {
		return proMap;
	}

	public void setProMap(HashMap<MagicSmeltRateCfg, Integer> proMap) {
		this.proMap = proMap;
	}
	
	public void addProMap(MagicSmeltRateCfg cfg){
		proMap.put(cfg, cfg.getWeight());
	}
}
