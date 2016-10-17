package com.playerdata.groupcompetition.prepare;

import java.util.List;
import java.util.Map;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.dataSyn.sameSceneSyn.SameSceneDataBaseIF;
import com.playerdata.dataSyn.sameSceneSyn.SameSceneSynDataIF;

@SynClass
public class SameSceneSynData implements SameSceneSynDataIF{
	
	private String Id;
	
	private Map<String, PositionInfo> synData;
	
	private List<String> addMembers;
	
	private List<String> removeMembers;

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

	public List<String> getAddMembers() {
		return addMembers;
	}

	public void setAddMembers(List<String> addMembers) {
		this.addMembers = addMembers;
	}

	public List<String> getRemoveMembers() {
		return removeMembers;
	}

	public void setRemoveMembers(List<String> removeMembers) {
		this.removeMembers = removeMembers;
	}
}
