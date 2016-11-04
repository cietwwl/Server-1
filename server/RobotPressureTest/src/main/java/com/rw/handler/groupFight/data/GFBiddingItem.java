package com.rw.handler.groupFight.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

/**
 * 个人压标信息
 * @author aken
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFBiddingItem implements SynItem{
	private String biddingID;  // biddingID = userID_resourceID

	private String resourceID;	//资源点
	
	private String userID;	//压标人
	
	private String bidGroup;  //所压公会

	private int rateID;	//压标倍率

	public String getId() {
		return biddingID;
	}

	public String getBiddingID() {
		return biddingID;
	}

	public void setBiddingID(String biddingID) {
		this.biddingID = biddingID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public String getBidGroup() {
		return bidGroup;
	}

	public void setBidGroup(String bidGroup) {
		this.bidGroup = bidGroup;
	}

	public int getRateID() {
		return rateID;
	}

	public void setRateID(int rateID) {
		this.rateID = rateID;
	}
}
