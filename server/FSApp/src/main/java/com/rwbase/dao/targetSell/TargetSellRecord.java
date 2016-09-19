package com.rwbase.dao.targetSell;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.playerdata.dataSyn.annotation.SynClass;

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
@SynClass
public class TargetSellRecord {

	
	private String userId;
	
	private int benefitScore;
	
	private List<BenefitItems> rewardItems;
	
	
	/**下次清除积分时间*/
	private long nextClearScoreTime;


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


	public List<BenefitItems> getRewardItems() {
		return rewardItems;
	}


	public void setRewardItems(List<BenefitItems> rewardItems) {
		this.rewardItems = rewardItems;
	}


	public long getNextClearScoreTime() {
		return nextClearScoreTime;
	}


	public void setNextClearScoreTime(long nextClearScoreTime) {
		this.nextClearScoreTime = nextClearScoreTime;
	}
	
	
	
	
}
