package com.groupCopy.bm.groupCopy;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupCopyDataVersion {

	private int groupCopyLevelData;
	
	private int groupCopyMapData;

	public int getGroupCopyLevelData() {
		return groupCopyLevelData;
	}

	public int getGroupCopyMapData() {
		return groupCopyMapData;
	}


}