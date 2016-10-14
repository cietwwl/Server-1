package com.rw.handler.groupCompetition.data.prepare;

import java.util.List;
import java.util.Map;

import com.rw.dataSyn.SynItem;

public class SameSceneSynData implements SynItem{
	
	private String Id;
	
	private Map<String, PositionInfo> synData;
	
	private List<String> addMembers;
	
	private List<String> removeMembers;

	public Map<String, PositionInfo> getSynData() {
		return synData;
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
