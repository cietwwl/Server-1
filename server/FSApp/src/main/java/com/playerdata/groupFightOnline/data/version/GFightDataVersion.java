package com.playerdata.groupFightOnline.data.version;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.SynDataGroupListVersion;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightDataVersion {
	
	private int resourceID;

	private int biddingItem;
	
	private int onlineGroupData;
	
	private int onlineResourceData;
	
	private int userOnlineData;
	
	private List<SynDataGroupListVersion> defendArmyItem;	
	

	public int getResourceID() {
		return resourceID;
	}

	public int getBiddingItem() {
		return biddingItem;
	}

	public List<SynDataGroupListVersion>  getDefendArmyItem() {
		return defendArmyItem;
	}

	public int getOnlineGroupData() {
		return onlineGroupData;
	}

	public int getOnlineResourceData() {
		return onlineResourceData;
	}

	public int getUserOnlineData() {
		return userOnlineData;
	}
}