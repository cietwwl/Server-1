package com.playerdata.dataSyn.sameSceneSyn;

import java.util.Map;

public interface SameSceneSynDataIF {
	public void setId(String id);
	public void setSynData(Map<String, ? extends SameSceneDataBaseIF> synData);
}
