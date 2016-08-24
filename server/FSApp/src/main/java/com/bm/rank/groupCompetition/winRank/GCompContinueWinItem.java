package com.bm.rank.groupCompetition.winRank;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
public class GCompContinueWinItem {

	private String userId;
	
	private String userName;
	
	private String headImage;	//头像
	
	private int continueWin;	//最高连胜次数
	
	private String groupName;

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

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public int getContinueWin() {
		return continueWin;
	}

	public void setContinueWin(int continueWin) {
		this.continueWin = continueWin;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
