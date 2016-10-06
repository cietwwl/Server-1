package com.playerdata.groupcompetition.quiz;

import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.groupcompetition.util.GCEventsType;

/**
 * 可被竞猜的帮派信息
 * @author aken
 */
@SynClass
public class QuizGroupInfo {

	private String groupId;
	
	private String groupName;
	
	private String groupIcon;
	
	private float rate;
	
	private long totalCoin;
	
	private int totalPlayer;

	public QuizGroupInfo(){ }
	
	public QuizGroupInfo(String groupId, String groupName, String groupIcon, float initRate){
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupIcon = groupIcon;
		this.rate = initRate;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getGroupIcon() {
		return groupIcon;
	}

	public synchronized long getTotalCoin() {
		return totalCoin;
	}

	public synchronized void addTotalCoin(long quizCoin) {
		this.totalCoin += quizCoin;
	}

	public synchronized int getTotalPlayer() {
		return totalPlayer;
	}

	public synchronized void addTotalPlayer() {
		this.totalPlayer++;
	}
	
	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public static String combineId(String groupId, GCEventsType eventType){
		return groupId + "_" + eventType.toString();
	}
}
