package com.playerdata.groupFightOnline.dataForRank;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GFGroupBiddingItem {
	private String groupID;
	private String groupName;
	private int totalBidding;	//竞标值

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getTotalBidding() {
		return totalBidding;
	}

	public void setTotalBidding(int totalBidding) {
		this.totalBidding = totalBidding;
	}
}
