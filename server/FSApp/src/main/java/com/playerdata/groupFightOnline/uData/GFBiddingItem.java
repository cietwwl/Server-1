package com.playerdata.groupFightOnline.uData;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gf_bidding_item")
public class GFBiddingItem implements IMapItem{
	@Id
	private String biddingID;  // biddingID = userID_resourceID

	private String resourceID;	//资源点
	
	@CombineSave
	private String userID;	//压标人
	
	@CombineSave
	private int bidGroup;  //所压公会
	
	@CombineSave
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

	public int getBidGroup() {
		return bidGroup;
	}

	public void setBidGroup(int bidGroup) {
		this.bidGroup = bidGroup;
	}

	public int getRateID() {
		return rateID;
	}

	public void setRateID(int rateID) {
		this.rateID = rateID;
	}
}
