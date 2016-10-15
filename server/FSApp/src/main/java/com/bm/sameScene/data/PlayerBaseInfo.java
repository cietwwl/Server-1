package com.bm.sameScene.data;

import com.playerdata.army.ArmyFashion;
import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class PlayerBaseInfo{

	String userId;//玩家ID
	
	String userName;//玩家名字
	
	int level;//玩家等级
	
	String imageId;//头像ID
	
	int career;//职业
	
	int sex;//性别
	
	int careerLevel = 7;//职业等级
	
	int fightingAll = 8;//总战斗力
	
	int modelId = 9;//模型ID
	
	ArmyFashion fashionUsage;//玩家穿戴的时装（如果玩家没有购买时装，则是没有指定）
	
	int starLevel;
	
	String qualityId;

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

	public ArmyFashion getFashionUsage() {
		return fashionUsage;
	}

	public void setFashionUsage(ArmyFashion fashionUsage) {
		this.fashionUsage = fashionUsage;
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
}
