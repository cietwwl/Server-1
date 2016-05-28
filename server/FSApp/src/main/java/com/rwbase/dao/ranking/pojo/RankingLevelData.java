package com.rwbase.dao.ranking.pojo;

public class RankingLevelData {
	private String userId;
	private int level;
	private long exp;
	private int fightingAll;
	private int fightingTeam;
	private int arenaPlace;
	private String userName;
	private String imageId;
	private String headbox;
	private int job;
	private int sex;
	private int careerLevel;
	private int modelId;
	private int rankLevel;
	private int rankCount;

	public int getRankLevel() {
		return rankLevel;
	}

	public void setRankLevel(int rankLevel) {
		this.rankLevel = rankLevel;
	}

	public int getRankCount() {
		return rankCount;
	}

	public void setRankCount(int rankCount) {
		this.rankCount = rankCount;
	}

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

	public void setFightingAll(int fighting) {
		this.fightingAll = fighting;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public String getUserName() {
		return userName == null ? "" : userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserHead() {
		return imageId;
	}

	public void setUserHead(String imageId) {
		this.imageId = imageId;
	}

	public String getHeadbox() {
		return headbox;
	}

	public void setHeadbox(String headbox) {
		this.headbox = headbox;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int templateId) {
		this.modelId = templateId;
	}

	public int getFightingTeam() {
		return fightingTeam;
	}

	public void setFightingTeam(int fightingTeam) {
		this.fightingTeam = fightingTeam;
	}

	public int getArenaPlace() {
		return arenaPlace;
	}

	public void setArenaPlace(int arenaPlace) {
		this.arenaPlace = arenaPlace;
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
}
