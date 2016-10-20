package com.rw.service.PeakArena.datamodel;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeakRecordHeroInfo {

	@JsonProperty("1")
	private String cfgId; // 模板id
	@JsonProperty("2")
	private int level; // 等级
	@JsonProperty("3")
	private String qualityId; // 品质id
	@JsonProperty("4")
	private long hpDamage; // hp伤害
	@JsonProperty("5")
	private boolean dead; // 是否已经死亡
	
	
	public String getCfgId() {
		return cfgId;
	}
	
	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}
	
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

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
}
