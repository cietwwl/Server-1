package com.rwbase.dao.setting.pojo;

public class HeadBoxCfg 
{
	private int order;
	private String atlas;
	private String spriteName;
	private String tips;
	private int type;
	private String littleThing;
	private String spriteId;
	
	public String getLittleThing() {
		return littleThing;
	}
	public void setLittleThing(String littleThing) {
		this.littleThing = littleThing;
	}
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
	public String getTips() {
		return tips;
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
	public void setTips(String tips) {
		this.tips = tips;
	}
	public String getSpriteId() {
		return spriteId;
	}
	public void setSpriteId(String spriteId) {
		this.spriteId = spriteId;
	}
}
