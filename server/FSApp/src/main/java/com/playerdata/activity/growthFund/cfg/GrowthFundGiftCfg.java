package com.playerdata.activity.growthFund.cfg;
import com.common.BaseConfig;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;

public class GrowthFundGiftCfg extends BaseConfig implements ActivitySubCfgIF{
	private int key; //关键字
	private int requiredLv; //需求等级
	private String rewardContents; //奖励内容

	public int getKey() {
		return key;
	}
	public int getRequiredLv() {
		return requiredLv;
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
