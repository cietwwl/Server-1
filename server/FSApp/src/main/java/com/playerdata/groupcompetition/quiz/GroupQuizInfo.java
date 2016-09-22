package com.playerdata.groupcompetition.quiz;

import javax.persistence.Id;

import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.rw.fsutil.dao.annotation.CombineSave;

@SynClass
public class GroupQuizInfo {
	
	@Id
	private String Id;	//groupId_stageId

	@IgnoreSynField
	@CombineSave
	private long totalQuizCoin;
	
	@CombineSave
	private int totalQuizPlayer;

	public String getId() {
		return Id;
	}

	public void setGroupId(String Id) {
		this.Id = Id;
	}

	public long getTotalQuizCoin() {
		return totalQuizCoin;
	}

	public synchronized void addTotalQuizCoin(long quizCoin) {
		this.totalQuizCoin += quizCoin;
	}

	public int getTotalQuizPlayer() {
		return totalQuizPlayer;
	}

	public synchronized void addTotalQuizPlayer() {
		this.totalQuizPlayer++;
	}
	
	public static String combineId(String groupId, String stageId){
		return groupId + "_" + stageId;
	}
}
