package com.playerdata.groupcompetition.fightRecord;

import com.playerdata.dataSyn.annotation.SynClass;


/**
 * 人物的简要信息，用于战斗记录
 * @author aken
 */
@SynClass
public class GCUserSimpleInfo {
	
	private String userName;	//角色名字
	
	private String groupName;	//角色公会名字
	
	private int continueWin;	//连胜场次
	
	private int personalScore;	//本场个人得分
	
	private int groupScore;		//本场帮派得分

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

	public int getContinueWin() {
		return continueWin;
	}

	public void setContinueWin(int continueWin) {
		this.continueWin = continueWin;
	}

	public int getPersonalScore() {
		return personalScore;
	}

	public void setPersonalScore(int personalScore) {
		this.personalScore = personalScore;
	}

	public int getGroupScore() {
		return groupScore;
	}

	public void setGroupScore(int groupScore) {
		this.groupScore = groupScore;
	}
}
