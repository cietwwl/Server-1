package com.playerdata.groupFightOnline.dataExtend;

public class GroupSimpleInfo {
	private String groupID;		//帮派ID
	
	private String icon;	//帮派图标
	
	private int level;	//帮派等级
	
	private String name;	//帮派名字
	
	private String leaderName;	//帮主名字

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
}
