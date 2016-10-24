package com.rwbase.dao.spriteattach.pojo;

import java.util.HashMap;
import java.util.Map;

public class SpriteAttachCfg {
	private int id;
	private String name;
	private String iconId;
	private int levelCostPlanId;
	private int levelPlanId;
	private int unlockPlanId;
	private int level;			//英雄等级需求
	private int quality;		//英雄品质需求
	private String preSprite;	//前置灵蕴需求
	
	//<灵蕴id,灵蕴等级>
	private Map<Integer, Integer> preSpriteRequireMap = new HashMap<Integer, Integer>();
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getIconId() {
		return iconId;
	}
	public int getLevelCostPlanId() {
		return levelCostPlanId;
	}
	public int getLevelPlanId() {
		return levelPlanId;
	}
	public int getUnlockPlanId() {
		return unlockPlanId;
	}
	public int getLevel() {
		return level;
	}
	public int getQuality() {
		return quality;
	}
	public String getPreSprite() {
		return preSprite;
	}
	public Map<Integer, Integer> getPreSpriteRequireMap() {
		return preSpriteRequireMap;
	}
	public void setPreSpriteRequireMap(Map<Integer, Integer> preSpriteRequireMap) {
		this.preSpriteRequireMap = preSpriteRequireMap;
	}
	
}
