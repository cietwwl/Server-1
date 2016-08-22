package com.playerdata.groupcompetition.prepare;

import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class SameSceneSynData {
	
	private String Id;
	
	private Map<String, PositionInfo> synData;

	public Map<String, PositionInfo> getSynData() {
		return synData;
	}

	public void setSynData(Map<String, PositionInfo> synData) {
		this.synData = synData;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}
}
