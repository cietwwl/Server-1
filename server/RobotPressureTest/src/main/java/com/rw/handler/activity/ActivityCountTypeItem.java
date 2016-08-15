package com.rw.handler.activity;

import java.util.ArrayList;
import java.util.List;



import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;



@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityCountTypeItem implements SynItem {

	private String id;
	
	private String userId;// 对应的角色Id
	
	private int count;

	private String cfgId;
	
	
	private boolean closed = false;

	
	
	private List<ActivityCountTypeSubItem> subItemList = new ArrayList<ActivityCountTypeSubItem>();
	
	
	
	private String version ;
	


	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}


	//重置活动
	public void reset(){
		subItemList = new ArrayList<ActivityCountTypeSubItem>();
		count = 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<ActivityCountTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityCountTypeSubItem> subItemList) {
		this.subItemList = subItemList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCfgId() {
		return cfgId;
	}

	public void setCfgId(String cfgId) {
		this.cfgId = cfgId;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	

	
	
	
}
