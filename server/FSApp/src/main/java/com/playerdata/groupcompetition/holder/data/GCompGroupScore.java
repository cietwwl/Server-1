package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompGroupScore {

	private String groupId; // 帮派的id
	private String groupName; // 帮派的名字
	private int score; // 当前的积分
	private String groupIcon; // 帮派的icon
	
	public static GCompGroupScore createNew(String groupId, String groupName, String groupIcon) {
		GCompGroupScore instance = new GCompGroupScore();
		instance.groupId = groupId;
		instance.groupName = groupName;
		instance.groupIcon = groupIcon;
		return instance;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public String getGroupIcon() {
		return groupIcon;
	}

	@Override
	public String toString() {
		return "GCompGroupScore [groupId=" + groupId + ", score=" + score + "]";
	}
}
