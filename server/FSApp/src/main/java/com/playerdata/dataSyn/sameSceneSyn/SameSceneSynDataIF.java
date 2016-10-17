package com.playerdata.dataSyn.sameSceneSyn;

import java.util.List;
import java.util.Map;

public interface SameSceneSynDataIF {
	public void setId(String id);
	public void setSynData(Map<String, ? extends SameSceneDataBaseIF> synData);
	public void setAddMembers(List<String> addMembers);
	public void setRemoveMembers(List<String> removeMembers);
}
