package com.groupCopy.bm.groupCopy;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCopyDataVersion {

	private int groupCopyLevelData;
	
	private int groupCopyMapData;
	
	private int groupCopyRewardData;

	public int getGroupCopyLevelData() {
		return groupCopyLevelData;
	}

	public int getGroupCopyMapData() {
		return groupCopyMapData;
	}

	public int getGroupCopyRewardData() {
		return groupCopyRewardData;
	}


}