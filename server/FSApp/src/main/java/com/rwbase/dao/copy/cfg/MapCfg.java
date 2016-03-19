package com.rwbase.dao.copy.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapCfg {
	private int id;
	private String name;
	private int level;
	private int group;
	private int levelType;
	private String description;
	private int startLevelId;
	private int endLevelId;
	private String levelPicture;
	private String rewardGain;
	private String rewardStar;

	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getLevelType() {
		return levelType;
	}
	public void setLevelType(int levelType) {
		this.levelType = levelType;
	}
	public String getRewardGain() {
		return rewardGain;
	}
	public void setRewardGain(String rewardGain) {
		this.rewardGain = rewardGain;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getStartLevelId() {
		return startLevelId;
	}
	public void setStartLevelId(int startLevelId) {
		this.startLevelId = startLevelId;
	}
	public int getEndLevelId() {
		return endLevelId;
	}
	public void setEndLevelId(int endLevelId) {
		this.endLevelId = endLevelId;
	}
	public String getLevelPicture() {
		return levelPicture;
	}
	public void setLevelPicture(String levelPicture) {
		this.levelPicture = levelPicture;
	}
	public String getRewardStar() {
		return rewardStar;
	}
	public void setRewardStar(String rewardStar) {
		this.rewardStar = rewardStar;
	}
	
}
