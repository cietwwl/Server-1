package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonProperty;

public class ActivityAliveGlobleData {
	
	@JsonProperty
	private HashMap<Integer, ArrayList<String>> activityMap;

	public HashMap<Integer, ArrayList<String>> getActivityMap() {
		return activityMap;
	}

	public void setActivityMap(HashMap<Integer, ArrayList<String>> activityMap) {
		this.activityMap = activityMap;
	}
}
