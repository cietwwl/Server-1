package com.rwbase.dao.ranking.pojo;

/**玩家基础数据，排行榜单太多，这个数据独立出来*/
public class RankingCommonData {
	private String userId;
	private int level;
	private long exp;
	private int fightingAll;
	private int fightingTeam;
	private String userName;
	private String imageId;
	private int job;
	private int sex;
	private int careerLevel;
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
		return userName;
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

	public void setFightingTeam(int teamFighting) {
		this.fightingTeam = teamFighting;
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
