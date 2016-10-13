package com.rw.handler.groupCompetition.data.guess;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.dataSyn.SynItem;
import com.rw.handler.groupCompetition.data.group.IGCGroup;

/**
 * 竞猜的项目
 * @author aken
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GCQuizEventItem implements SynItem{
	
	private int matchId;
	
	private QuizGroupInfo groupA;
	
	private QuizGroupInfo groupB;
	
	private int baseCoin;
	
	private String winGroupId;

	private int sessionId;	//第几届

	private String eventsType;	//赛事阶段

	private int fightNum;	//第几场

	public GCQuizEventItem(){	}
	
	public GCQuizEventItem(int sessionId, String eventsType, int fightNum, int matchId, int baseCoin, IGCGroup groupA, IGCGroup groupB, float initRate){
		this.sessionId = sessionId;
		this.eventsType = eventsType;
		this.fightNum = fightNum;
		this.matchId = matchId;
		this.baseCoin = baseCoin;
		this.groupA = new QuizGroupInfo(groupA.getGroupId(), groupA.getGroupName(), groupA.getIcon(), initRate);
		this.groupB = new QuizGroupInfo(groupB.getGroupId(), groupB.getGroupName(), groupB.getIcon(), initRate);
	}
	
	public String getId(){
		return String.valueOf(matchId);
	}
	
	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}
	
	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public String getEventsType() {
		return eventsType;
	}

	public void setEventsType(String eventsType) {
		this.eventsType = eventsType;
	}

	public int getFightNum() {
		return fightNum;
	}

	public void setFightNum(int fightNum) {
		this.fightNum = fightNum;
	}
	
	public QuizGroupInfo getGroupA() {
		return groupA;
	}

	public void setGroupA(QuizGroupInfo groupA) {
		this.groupA = groupA;
	}

	public QuizGroupInfo getGroupB() {
		return groupB;
	}

	public void setGroupB(QuizGroupInfo groupB) {
		this.groupB = groupB;
	}

	public int getBaseCoin() {
		return baseCoin;
	}

	public void setBaseCoin(int baseCoin) {
		this.baseCoin = baseCoin;
	}
	
	public String getWinGroupId() {
		return winGroupId;
	}

	public void setWinGroupId(String winGroupId) {
		this.winGroupId = winGroupId;
	}

	public QuizGroupInfo getQuizGroupInfo(String groupId){
		if(StringUtils.equals(groupId, groupA.getGroupId())){
			return groupA;
		}else if(StringUtils.equals(groupId, groupB.getGroupId())){
			return groupB;
		}
		return null;
	}
	
	public QuizGroupInfo getWinQuizGroup(){
		if(StringUtils.equals(winGroupId, groupA.getGroupId())){
			return groupA;
		}else if(StringUtils.equals(winGroupId, groupB.getGroupId())){
			return groupB;
		}
		return null;
	}
}
