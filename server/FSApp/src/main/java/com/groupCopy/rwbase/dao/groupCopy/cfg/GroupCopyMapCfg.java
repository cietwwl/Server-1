package com.groupCopy.rwbase.dao.groupCopy.cfg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCopyMapCfg {
    private String id; //副本地图ID...
    private String name; //名称...
    private int levelType; //关卡类型...
    private int level; //解锁等级...
    private String description; //描述...
    private int startLevelId; //开始关卡ID...
    private int endLevelId; //结束关卡...
    private String levelPicture; //章节底图...
    private int openCost; //开启消耗...
    private int timeExtraReward; //时间额外奖励...
    private int damageExtraReward; //伤害额外奖励...
    private int passaReward; //通关奖励...
    private int damageNum; //伤害较高额外奖励人数...
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevelType() {
		return levelType;
	}
	public void setLevelType(int levelType) {
		this.levelType = levelType;
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
	public int getOpenCost() {
		return openCost;
	}
	public void setOpenCost(int openCost) {
		this.openCost = openCost;
	}
	public int getTimeExtraReward() {
		return timeExtraReward;
	}
	public void setTimeExtraReward(int timeExtraReward) {
		this.timeExtraReward = timeExtraReward;
	}
	public int getDamageExtraReward() {
		return damageExtraReward;
	}
	public void setDamageExtraReward(int damageExtraReward) {
		this.damageExtraReward = damageExtraReward;
	}
	public int getPassaReward() {
		return passaReward;
	}
	public void setPassaReward(int passaReward) {
		this.passaReward = passaReward;
	}
	public int getDamageNum() {
		return damageNum;
	}
	public void setDamageNum(int damageNum) {
		this.damageNum = damageNum;
	}
	
    
    
}
