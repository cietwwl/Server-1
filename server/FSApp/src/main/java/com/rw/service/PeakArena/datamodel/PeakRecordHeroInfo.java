package com.rw.service.PeakArena.datamodel;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * 
 * @author CHEN.P
 *
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeakRecordHeroInfo {

	@JsonProperty("1")
	private String heroId; // 英雄的ID
	@JsonProperty("2")
	private String headImage; // 头像（主角才有）
	@JsonProperty("3")
	private int modelId; // 英雄数据的modelId
	@JsonProperty("4")
	private int level; // 等级
	@JsonProperty("5")
	private int starLv; // 星级
	@JsonProperty("6")
	private String qualityId; // 品质id
	@JsonProperty("7")
	private long hpDamage; // HP伤害
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getQualityId() {
		return qualityId;
	}
	
	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}

	public long getHpDamage() {
		return hpDamage;
	}

	public void setHpDamage(long hpDamage) {
		this.hpDamage = hpDamage;
	}

	public String getHeroId() {
		return heroId;
	}

	public void setHeroId(String heroId) {
		this.heroId = heroId;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getStarLv() {
		return starLv;
	}

	public void setStarLv(int starLv) {
		this.starLv = starLv;
	}
}
