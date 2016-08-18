package com.playerdata.groupcompetition.syn;

import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class SameSceneSynData {
	
	private String Id;
	
	private Map<String, Object> synData;

	public Map<String, Object> getSynData() {
		return synData;
	}

	public void setSynData(Map<String, Object> synData) {
		this.synData = synData;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}
}
