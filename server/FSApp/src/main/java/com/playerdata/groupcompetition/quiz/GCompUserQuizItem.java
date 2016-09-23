package com.playerdata.groupcompetition.quiz;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.cacheDao.mapItem.IMapItem;
import com.rw.fsutil.dao.annotation.CombineSave;

/**
 * 个人竞猜信息
 * @author aken
 */
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gc_quiz_item")
public class GCompUserQuizItem implements IMapItem{
	@Id
	private String id;  // id = userID_matchId

	private String userID;	//竞猜的角色id
	
	@CombineSave
	private int matchId;	//比赛的id

	@CombineSave
	private int coinCount;	//竞猜花费的金币
	
	@CombineSave
	private String groupId;  //竞猜的帮派
	
	@CombineSave
	private boolean isGetReward = false;	//是否已经领取过奖励
	
	@CombineSave
	@IgnoreSynField
	private int sessionId;	//第几届

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

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	
	public static String conbineId(String userID, int matchId){
		return userID + "_" + matchId;
	}
}
