package com.rwbase.dao.activityTime;

import java.util.List;

public class ActCfgInfo {
	
	private int platformVersion;
	
	private List<SingleActTime> actList;

	public int getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(int platformVersion) {
		this.platformVersion = platformVersion;
	}

	public List<SingleActTime> getActList() {
		return actList;
	}

	public void setActList(List<SingleActTime> actList) {
		this.actList = actList;
	}

}
