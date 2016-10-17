package com.playerdata.groupcompetition.holder.data;

import com.playerdata.dataSyn.annotation.SynClass;

@SynClass
public class GCompGroupSynData {

	private String groupId; // 帮派id
	private String groupName; // 帮派名字
	private String leaderName; // 帮主名字
	private String assistantName; // 副帮主名字
	private int gCompScore; // 比赛积分
	private int gCompPower; // 帮派战力
	private int historyNum; // 历史记录的数量
	private int upNum; // 上升下降的名次
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getLeaderName() {
		return leaderName;
	}
	
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	
	public String getAssistantName() {
		return assistantName;
	}
	
	public void setAssistantName(String assistantName) {
		this.assistantName = assistantName;
	}
	
	public int getgCompScore() {
		return gCompScore;
	}
	
	public void setGCompScore(int gCompScore) {
		this.gCompScore = gCompScore;
	}
	
	public int getgCompPower() {
		return gCompPower;
	}
	
	public void setGCompPower(int gCompPower) {
		this.gCompPower = gCompPower;
	}
	public int getHistoryNum() {
		return historyNum;
	}
	
	public void setHistoryNum(int historyNum) {
		this.historyNum = historyNum;
	}
	
	public int getUpNum() {
		return upNum;
	}
	
	public void setUpNum(int upNum) {
		this.upNum = upNum;
	}
	
}
