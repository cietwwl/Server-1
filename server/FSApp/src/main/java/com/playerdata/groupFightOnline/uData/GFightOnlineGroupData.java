package com.playerdata.groupFightOnline.uData;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightOnlineGroupData {
	
	@Id
	private int groupID;
	
	@CombineSave
	private int biddingCount;  // 帮派竞标用的令牌数
	
	@CombineSave
	private int resourceID;		// 帮派竞标的资源点
	
	@CombineSave
	private long lastBidTime;	// 上次竞标时间，主要用于排名

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	public int getBiddingCount() {
		return biddingCount;
	}

	public void setBiddingCount(int biddingCount) {
		this.biddingCount = biddingCount;
	}

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}
}
