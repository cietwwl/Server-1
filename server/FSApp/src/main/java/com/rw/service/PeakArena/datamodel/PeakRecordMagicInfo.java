package com.rw.service.PeakArena.datamodel;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeakRecordMagicInfo {

	@JsonProperty("1")
	private int cfgId;
	@JsonProperty("2")
	private int level;
	
	public int getCfgId() {
		return cfgId;
	}
	
	public void setCfgId(int cfgId) {
		this.cfgId = cfgId;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
}
