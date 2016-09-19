package com.playerdata.groupcompetition.prepare;

import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.dataSyn.sameSceneSyn.SameSceneDataBaseIF;
import com.playerdata.dataSyn.sameSceneSyn.SameSceneSynDataIF;

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
