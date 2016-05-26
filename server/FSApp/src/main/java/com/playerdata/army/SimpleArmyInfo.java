package com.playerdata.army;

import java.util.List;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class SimpleArmyInfo {
	private List<String> heroIds;
	private int modelId;
	private int level;
	
	public List<String> getHeroIds() {
		return heroIds;
	}
	
	public void setHeroIds(List<String> heroIds) {
		this.heroIds = heroIds;
	}
	
	public int getModelId() {
		return modelId;
	}
	
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
}
