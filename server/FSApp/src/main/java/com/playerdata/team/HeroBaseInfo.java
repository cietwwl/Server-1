package com.playerdata.team;

/*
 * @author HC
 * @date 2016年4月15日 下午5:57:19
 * @Description 
 */
public class HeroBaseInfo {
	private int level;// 等级
	private String quality;// 品质
	private int star;// 星级
	private String tmpId;// 模版Id

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String getTmpId() {
		return tmpId;
	}

	public void setTmpId(String tmpId) {
		this.tmpId = tmpId;
	}
}