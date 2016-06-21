package com.playerdata.groupFightOnline.dataExtend;

public class GFResourceInfo {
	private int resourceID;	//资源点ID
	
	private GroupSimpleInfo groupInfo;		//占有公会简要信息

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public GroupSimpleInfo getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(GroupSimpleInfo groupInfo) {
		this.groupInfo = groupInfo;
	}
}
