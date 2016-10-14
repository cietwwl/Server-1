package com.rw.handler.groupCompetition.data.guess;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;

/**
 * 个人竞猜信息
 * @author aken
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GCompUserQuizItem implements SynItem{
	
	private String id;  // id = userID_matchId

	private String userID;	//竞猜的角色id

	private int matchId;	//比赛的id

	private int coinCount;	//竞猜花费的金币
	
	private String groupId;  //竞猜的帮派
	
	private boolean isGetReward = false;	//是否已经领取过奖励

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public int getCoinCount() {
		return coinCount;
	}

	public void setCoinCount(int coinCount) {
		this.coinCount = coinCount;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public boolean isGetReward() {
		return isGetReward;
	}

	public void setGetReward(boolean isGetReward) {
		this.isGetReward = isGetReward;
	}
	
	public static String conbineId(String userID, int matchId){
		return userID + "_" + matchId;
	}
}
