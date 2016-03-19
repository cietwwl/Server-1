package com.rwbase.dao.guide.pojo;

import java.util.HashMap;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_guide")
public class TableGuide {

	@Id
	private String userId;
	private HashMap<Integer, GuideData> guideMap = new HashMap<Integer, GuideData>();
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public HashMap<Integer, GuideData> getGuideMap() {
		return guideMap;
	}
	public void setGuideMap(HashMap<Integer, GuideData> guideMap) {
		this.guideMap = guideMap;
	}

}
