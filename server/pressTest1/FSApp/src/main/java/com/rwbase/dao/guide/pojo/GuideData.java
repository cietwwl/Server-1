package com.rwbase.dao.guide.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rwbase.common.enu.eGuideStateDef;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuideData {
	private int id;
	private eGuideStateDef state;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public eGuideStateDef getState() {
		return state;
	}
	public void setState(eGuideStateDef state) {
		this.state = state;
	}
}
