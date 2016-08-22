package com.playerdata.groupcompetition.prepare;

import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.syn.SameSceneDataBaseIF;
import com.playerdata.groupcompetition.syn.SameSceneSynDataIF;

@SynClass
public class SameSceneSynData implements SameSceneSynDataIF{
	
	private String Id;
	
	private Map<String, PositionInfo> synData;

	public Map<String, PositionInfo> getSynData() {
		return synData;
	}

	@SuppressWarnings("unchecked")
	public void setSynData(Map<String, ? extends SameSceneDataBaseIF> synData) {
		this.synData = (Map<String, PositionInfo>) synData;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}
}
