package com.rwbase.dao.worship.pojo;


public class WorshipItem {
	private String userId;
	private int level;
	private int fightingAll;
	private String userName;
	private String imageId;
	private int career;
	private int sex;
	private int careerLevel;
	private long worshipTime;
	private boolean canReceive;
	private WorshipItemData itemData;
	private int modelId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getFightingAll() {
		return fightingAll;
	}
	public void setFightingAll(int fightingAll) {
		this.fightingAll = fightingAll;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public int getCareer() {
		return career;
	}
	public void setCareer(int career) {
		this.career = career;
	}
	public boolean isCanReceive() {
		return canReceive;
	}
	public void setCanReceive(boolean canReceive) {
		this.canReceive = canReceive;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getCareerLevel() {
		return careerLevel;
	}
	public void setCareerLevel(int careerLevel) {
		this.careerLevel = careerLevel;
	}
	public WorshipItemData getItemData() {
		return itemData;
	}
	public void setItemData(WorshipItemData itemData) {
		this.itemData = itemData;
	}
	public long getWorshipTime() {
		return worshipTime;
	}
	public void setWorshipTime(long worshipTime) {
		this.worshipTime = worshipTime;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
}
