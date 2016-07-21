package com.playerdata.groupFightOnline.dataForRank;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GFOnlineKillItem {
	private String userId;
	private String userName;
	private int totalKill;	//总伤害值
	private String groupID;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getTotalKill() {
		return totalKill;
	}

	public void setTotalKill(int totalKill) {
		this.totalKill = totalKill;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
}
