package com.playerdata.team;

import org.codehaus.jackson.annotate.JsonIgnore;

/*
 * @author HC
 * @date 2016年7月13日 下午7:01:50
 * @Description 
 */
public class HeroFixEquipInfo {
	private String id;
	private int level;
	private int quality;
	private int star;
	@JsonIgnore
	private int slot; // 战斗力计算那边借用这个变量识别，但是不需要保存到数据库

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}
	
	@JsonIgnore
	public int getSlot() {
		return slot;
	}
	
	@JsonIgnore
	public void setSlot(int pSlot) {
		this.slot = pSlot;
	}
}