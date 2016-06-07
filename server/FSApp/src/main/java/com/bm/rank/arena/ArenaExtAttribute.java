package com.bm.rank.arena;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.bm.arena.ArenaConstant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArenaExtAttribute extends FightingMember {

	private int career; // 职业
	private int fighting; // 战力
	private String name; // 名称
	private String headImage; // 头像
	private String headbox; //头像框
	private int level; // 等级

	private int fightingTeam; //4个最高战力的佣兵的战力总和
	private int sex;
	private int modelId;
	private int rankLevel;
	private int rankCount;

	public ArenaExtAttribute() {
		super(ArenaConstant.ARENA_FIGHTING_TIMEOUT);
	}

	public ArenaExtAttribute(int career, int fighting, String name, String headImage, int level) {
		super(ArenaConstant.ARENA_FIGHTING_TIMEOUT);
		this.career = career;
		this.fighting = fighting;
		this.name = name;
		this.headImage = headImage;
		this.level = level;
	}

	public int getCareer() {
		return career;
	}

	public void setCareer(int career) {
		this.career = career;
	}

	public int getFighting() {
		return fighting;
	}

	public void setFighting(int fighting) {
		this.fighting = fighting;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFightingTeam() {
		return fightingTeam;
	}

	public void setFightingTeam(int fightingTeam) {
		this.fightingTeam = fightingTeam;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

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

	public String getHeadbox() {
		return headbox;
	}

	public void setHeadbox(String headbox) {
		this.headbox = headbox;
	}

}
