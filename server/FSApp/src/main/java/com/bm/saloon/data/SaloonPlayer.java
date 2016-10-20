package com.bm.saloon.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class SaloonPlayer {

	private String userId;//玩家ID
	
	private String userName;//玩家名字
	
	private int level;//玩家等级
	
	private String imageId;//头像ID
	
	private int career;//职业
	
	private int sex;//性别
	
	private int careerLevel = 7;//职业等级
	
	private int fightingAll = 8;//总战斗力
	
	private int modelId = 9;//模型ID
	
	private int starLevel;
	
	private String qualityId;
	
	private SaloonPlayerFashion playerFashion;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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

	public int getFightingAll() {
		return fightingAll;
	}

	public void setFightingAll(int fightingAll) {
		this.fightingAll = fightingAll;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(int starLevel) {
		this.starLevel = starLevel;
	}

	public String getQualityId() {
		return qualityId;
	}

	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}

	public SaloonPlayerFashion getPlayerFashion() {
		return playerFashion;
	}

	public void setPlayerFashion(SaloonPlayerFashion playerFashion) {
		this.playerFashion = playerFashion;
	}
	
	
	
	
}
