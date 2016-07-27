package com.playerdata.team;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

/*
 * @author HC
 * @date 2016年4月15日 下午5:57:19
 * @Description 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SynClass
public class HeroBaseInfo {
	private int level;// 等级
	private String quality;// 品质
	private int star;// 星级
	private String tmpId;// 模版Id
	private int pos;// 站位

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

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
}