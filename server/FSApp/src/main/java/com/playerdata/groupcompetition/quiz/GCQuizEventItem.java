package com.playerdata.groupcompetition.quiz;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;

/**
 * 竞猜的项目
 * @author aken
 */
@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gc_quiz_event")
public class GCQuizEventItem {
	
	@Id
	private int matchId;
	
	@CombineSave
	private QuizGroupInfo groupA;
	
	@CombineSave
	private QuizGroupInfo groupB;
	
	@CombineSave
	private int baseCoin;
	
	@CombineSave
	private String winGroupId;
	
	@CombineSave
	private int sessionId;	//第几届
	
	@IgnoreSynField
	@CombineSave
	private boolean isFinalRate = false;
	
	@IgnoreSynField
	@NonSave
	public static float DEFAULT_MIN_RATE = 1.1f;

	public GCQuizEventItem(){	}
	
	public GCQuizEventItem(int sessionId, int matchId, int baseCoin, IGCGroup groupA, IGCGroup groupB, float initRate){
		this.sessionId = sessionId;
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
			groupA.setRate(rate < DEFAULT_MIN_RATE ? DEFAULT_MIN_RATE : rate);
		}
		if(bCoin != 0){
			float rate = (float)(totalCoin/(bCoin * 1.0));
			groupB.setRate(rate < DEFAULT_MIN_RATE ? DEFAULT_MIN_RATE : rate);
		}
		this.isFinalRate = isFinalRate;
	}
}
