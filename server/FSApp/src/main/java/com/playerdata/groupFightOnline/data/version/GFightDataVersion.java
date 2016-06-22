package com.playerdata.groupFightOnline.data.version;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GFightDataVersion {

	private int biddingItem;
	
	private int defendArmyItem;
	
	private int onlineGroupData;
	
	private int onlineResourceData;
	
	private int userOnlineData;

	public int getBiddingItem() {
		return biddingItem;
	}

	public int getDefendArmyItem() {
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