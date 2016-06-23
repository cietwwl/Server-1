package com.playerdata.groupFightOnline.dataForClient;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupFightOnline.dataForRank.GFGroupBiddingItem;

/**
 * 资源点信息（打开资源点界面时用到）
 * @author aken
 */
@SynClass
public class GFResourceInfo {
	private int resourceID;	//资源点ID
	
	private GFGroupBiddingItem groupInfo;		//占有公会简要信息

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public GFGroupBiddingItem getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(GFGroupBiddingItem groupInfo) {
		this.groupInfo = groupInfo;
	}
}
