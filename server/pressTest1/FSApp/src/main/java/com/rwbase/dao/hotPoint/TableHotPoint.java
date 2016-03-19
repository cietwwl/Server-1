package com.rwbase.dao.hotPoint;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "mt_table_hotpoint")
public class TableHotPoint {
	@Id
	private String userId;
	private Map<EHotPointType, Boolean> hotPointList = new HashMap<EHotPointType, Boolean>();
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Map<EHotPointType, Boolean> getHotPointList() {
		return hotPointList;
	}
	public void setHotPointList(Map<EHotPointType, Boolean> hotPointList) {
		this.hotPointList = hotPointList;
	}
}
