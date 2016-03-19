package com.rwbase.dao.task.pojo;

public class TaskCfg {

	private int id;
	private int quality;
	private String title;
	private String icon;
	private String desc;
	private int superType;
	private int preTask;
	private int finishType;
	private String finishParam;
	private String reward;
	private int achievementPoint;
	private String skip;
	private int openLevel;
	private String altar;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getReward() {
		return reward;
	}
	public void setReward(String reward) {
		this.reward = reward;
	}
	public int getAchievementPoint() {
		return achievementPoint;
	}
	public void setAchievementPoint(int achievementPoint) {
		this.achievementPoint = achievementPoint;
	}
	public String getSkip() {
		return skip;
	}
	public void setSkip(String skip) {
		this.skip = skip;
	}
	public int getSuperType() {
		return superType;
	}
	public void setSuperType(int superType) {
		this.superType = superType;
	}
	public int getPreTask() {
		return preTask;
	}
	public void setPreTask(int preTask) {
		this.preTask = preTask;
	}
	public int getFinishType() {
		return finishType;
	}
	public void setFinishType(int finishType) {
		this.finishType = finishType;
	}
	public String getFinishParam() {
		return finishParam;
	}
	public void setFinishParam(String finishParam) {
		this.finishParam = finishParam;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public int getOpenLevel() {
		return openLevel;
	}
	public void setOpenLevel(int openLevel) {
		this.openLevel = openLevel;
	}
	public String getAltar() {
		return altar;
	}
	public void setAltar(String altar) {
		this.altar = altar;
	}
}
