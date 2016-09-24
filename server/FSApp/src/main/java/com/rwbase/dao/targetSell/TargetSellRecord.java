package com.rwbase.dao.targetSell;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@SynClass
public class TargetSellRecord {

	
	private String userId;
	
	private int benefitScore;
	
	private Map<Integer, BenefitItems> itemMap;
	
	
	/**下次清除积分时间*/
	private long nextClearScoreTime;


	/**已经领取的道具<道具组id,还可领取次数>*/
	private Map<Integer, Integer> recieveMap;
	
	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public int getBenefitScore() {
		return benefitScore;
	}


	public void setBenefitScore(int benefitScore) {
		this.benefitScore = benefitScore;
	}



	public long getNextClearScoreTime() {
		return nextClearScoreTime;
	}


	public void setNextClearScoreTime(long nextClearScoreTime) {
		this.nextClearScoreTime = nextClearScoreTime;
	}


	public Map<Integer, BenefitItems> getItemMap() {
		return itemMap;
	}


	public void setItemMap(Map<Integer, BenefitItems> itemMap) {
		this.itemMap = itemMap;
	}


	public Map<Integer, Integer> getRecieveMap() {
		return recieveMap;
	}


	public void setRecieveMap(Map<Integer, Integer> recieveMap) {
		this.recieveMap = recieveMap;
	}
	
	
	
	
}
