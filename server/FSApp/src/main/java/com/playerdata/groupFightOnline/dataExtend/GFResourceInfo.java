package com.playerdata.groupFightOnline.dataExtend;

public class GFResourceInfo {
	private int resourceID;	//资源点ID
	
	private GFGroupSimpleInfo groupInfo;		//占有公会简要信息

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public GFGroupSimpleInfo getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(GFGroupSimpleInfo groupInfo) {
		this.groupInfo = groupInfo;
	}
}
