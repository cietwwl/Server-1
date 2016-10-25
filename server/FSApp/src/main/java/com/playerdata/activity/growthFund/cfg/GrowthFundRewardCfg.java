package com.playerdata.activity.growthFund.cfg;
import com.common.BaseConfig;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;

public class GrowthFundRewardCfg extends BaseConfig implements ActivitySubCfgIF{
	
	private int key; //关键字
	private int requiredCount; //需求人数
	private String rewardContents; //奖励内容

	public int getKey() {
		return key;
	}
	
	public int getRequiredCount() {
		return requiredCount;
	}
	
	public String getRewardContents() {
		return rewardContents;
	}
	
	@Override
	public int getId() {
		return key;
	}
	
	@Override
	public String getDay() {
		return "1";
	}
	
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}
}
