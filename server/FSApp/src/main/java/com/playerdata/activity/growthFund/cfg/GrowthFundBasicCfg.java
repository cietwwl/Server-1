package com.playerdata.activity.growthFund.cfg;
import com.common.BaseConfig;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;

public class GrowthFundBasicCfg extends BaseConfig implements ActivityCfgIF{
	
	private int key; //关键字
	private int vipLv; //可购买的VIP等级
	private int price; //价格

	public int getKey() {
		return key;
	}
	
	public int getVipLv() {
		return vipLv;
	}
	
	public int getPrice() {
		return price;
	}

	@Override
	public int getId() {
		return key;
	}

	@Override
	public long getStartTime() {
		return 0;
	}

	@Override
	public long getEndTime() {
		return Long.MAX_VALUE;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public int getLevelLimit() {
		return 0;
	}

	@Override
	public boolean isDailyRefresh() {
		return false;
	}
}
