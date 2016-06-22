package com.playerdata.groupFightOnline.dataForRank;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GFOnlineHurtItem {
	private String userId;
	private String userName;
	private int totalHurt;	//总伤害值
	private String groupID;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getTotalHurt() {
		return totalHurt;
	}

	public void setTotalHurt(int totalHurt) {
		this.totalHurt = totalHurt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}	
}
