package com.playerdata.groupFightOnline.dataExtend;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GFUserSimpleInfo {
	
	private String userName;	//角色名字
	
	private String groupName;	//角色公会名字
	
	private String playerHeadFrame;	//头像框
	
	private String playerHeadImage;	//头像

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getPlayerHeadFrame() {
		return playerHeadFrame;
	}

	public void setPlayerHeadFrame(String playerHeadFrame) {
		this.playerHeadFrame = playerHeadFrame;
	}

	public String getPlayerHeadImage() {
		return playerHeadImage;
	}

	public void setPlayerHeadImage(String playerHeadImage) {
		this.playerHeadImage = playerHeadImage;
	}
}
