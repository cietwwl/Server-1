package com.playerdata.groupFightOnline.dataForClient;

/**
 * 资源点信息（打开资源点界面时用到）
 * @author aken
 */
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
