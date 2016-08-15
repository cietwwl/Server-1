package com.rw.handler.groupFight.dataForClient;

import com.rw.handler.groupFight.dataForRank.GFGroupBiddingItem;

/**
 * 资源点信息（打开资源点界面时用到）
 * @author aken
 */
public class GFResourceInfo {
	private int resourceID;	//资源点ID
	
	private GFGroupBiddingItem groupInfo;		//占有公会简要信息
	
	/**
	 * 资源点的状态信息
	 * 0, 初始化阶段（该阶段只能跳去休战和竞标阶段）
	 * 1, 休战期间（只能跳去竞标阶段）
	 * 2, 竞标阶段（只能跳去备战阶段）
	 * 3, 备战阶段（只能跳去开战阶段）
	 * 4, 开战阶段（只能跳去休战阶段）
	 */
	private int state;

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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
