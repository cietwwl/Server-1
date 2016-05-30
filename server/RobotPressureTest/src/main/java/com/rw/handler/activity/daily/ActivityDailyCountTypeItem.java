package com.rw.handler.activity.daily;

import java.util.ArrayList;
import java.util.List;




import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;



@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDailyCountTypeItem implements  SynItem {

	private String id;
	
	private String userId;// 对应的角色Id
    private String cfgid;
	
	public String getCfgid() {
		return cfgid;
	}

	public void setCfgid(String cfgid) {
		this.cfgid = cfgid;
	}

	private boolean closed = false;

	private long lastTime;
	
	private List<ActivityDailyCountTypeSubItem> subItemList = new ArrayList<ActivityDailyCountTypeSubItem>();
	
	
	private String version ;
	


	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}




	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public List<ActivityDailyCountTypeSubItem> getSubItemList() {
		return subItemList;
	}

	public void setSubItemList(List<ActivityDailyCountTypeSubItem> subItemList) {
		this.subItemList = subItemList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}



	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	

	
	
	
}
