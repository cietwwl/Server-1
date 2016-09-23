package com.playerdata.groupcompetition.quiz;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.data.IGCGroup;

/**
 * 竞猜的项目
 * @author aken
 */
@SynClass
public class GCQuizEventItem {
	
	private int matchId;
	
	private QuizGroupInfo groupA;
	
	private QuizGroupInfo groupB;
	
	private int baseCoin;
	
	private String winGroupId;
	
	@IgnoreSynField
	private boolean isFinalRate = false;
	
	@IgnoreSynField
	public static float DEFAULT_RATE = 1.1f;

	public GCQuizEventItem(){	}
	
	public GCQuizEventItem(int matchId, int baseCoin, IGCGroup groupA, IGCGroup groupB, float initRate){
		this.matchId = matchId;
		this.baseCoin = baseCoin;
		this.groupA = new QuizGroupInfo(groupA.getGroupId(), groupA.getGroupName(), groupA.getIcon(), initRate);
		this.groupB = new QuizGroupInfo(groupB.getGroupId(), groupB.getGroupName(), groupB.getIcon(), initRate);
	}
	
	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
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
	
	/**
	 * 刷新赔率
	 */
	public void refreshRate(boolean isFinalRate){
		if(this.isFinalRate) return; 
		long aCoin = groupA.getTotalCoin();
		long bCoin = groupB.getTotalCoin();
		long totalCoin = aCoin + bCoin + baseCoin;
		if(aCoin != 0){
			float rate = (float)(totalCoin/(aCoin * 1.0));
			groupA.setRate(rate < DEFAULT_RATE ? DEFAULT_RATE : rate);
		}
		if(bCoin != 0){
			float rate = (float)(totalCoin/(bCoin * 1.0));
			groupB.setRate(rate < DEFAULT_RATE ? DEFAULT_RATE : rate);
		}
		this.isFinalRate = isFinalRate;
	}
}
