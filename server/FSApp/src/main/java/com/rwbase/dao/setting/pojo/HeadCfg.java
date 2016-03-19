package com.rwbase.dao.setting.pojo;

public class HeadCfg 
{
	private int order;
	private String atlas;
	private String spriteName;
	private String spriteId;
	private int career;
	private int rank;
	private int sex;
	private int type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getOrder() {
		return order;
	}
	public String getAtlas() {
		return atlas;
	}
	public String getSpriteName() {
		return spriteName;
	}
	public int getCareer() {
		return career;
	}
	public int getRank() {
		return rank;
	}
	public int getSex() {
		return sex;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public void setAtlas(String atlas) {
		this.atlas = atlas;
	}
	public void setSpriteName(String spriteName) {
		this.spriteName = spriteName;
	}
	public void setCareer(int career) {
		this.career = career;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getSpriteId() {
		return spriteId;
	}
	public void setSpriteId(String spriteId) {
		this.spriteId = spriteId;
	}
}
