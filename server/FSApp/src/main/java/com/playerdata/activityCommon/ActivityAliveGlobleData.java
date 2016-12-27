package com.playerdata.activityCommon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.annotate.JsonProperty;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;

public class ActivityAliveGlobleData {
	
	@JsonProperty
	private HashMap<Integer, ArrayList<String>> activityMap;

	public HashMap<Integer, ArrayList<String>> getActivityMap() {
		return activityMap;
	}

	public void setActivityMap(HashMap<Integer, ArrayList<String>> activityMap) {
		this.activityMap = activityMap;
	}
	
	public void setActivityToGlobleMap(Map<Integer, HashMap<String, ? extends ActivityCfgIF>> activityMapDetail) {
		this.activityMap = new HashMap<Integer, ArrayList<String>>();
		if(null != activityMapDetail){
			for(Entry<Integer, HashMap<String, ? extends ActivityCfgIF>> entry : activityMapDetail.entrySet()){
				this.activityMap.put(entry.getKey(), new ArrayList<String>(entry.getValue().keySet()));
			}
		}
	}
}
