package com.playerdata.activity.growthFund.cfg;
import com.common.BaseConfig;
import com.playerdata.activity.growthFund.GrowthFundType;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;

public class GrowthFundBasicCfg extends BaseConfig implements ActivityCfgIF{
	
	private int key; //关键字
	private int vipLv; //可购买的VIP等级
	private int price; //价格
	private int levelLimit;	//等级限制
	private int type; // 成长基金类型
	private GrowthFundType _fundType; // 类型的枚举形式
	private long startTime = 0;
	private long endTime = Long.MAX_VALUE;
	
	public int getKey() {
		return key;
	}
	
	public int getCfgId() {
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
		return startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public int getVersion() {
		return 0;
	}
	
	@Override
	public int getLevelLimit() {
		return levelLimit;
	}

	@Override
	public boolean isDailyRefresh() {
		return false;
	}

	public int getType() {
		return type;
	}
	
	public GrowthFundType getFundType() {
		return _fundType;
	}
	
	@Override
	public void ExtraInitAfterLoad() {
		this._fundType = GrowthFundType.getBySign(type);
	}

	@Override
	public int getVipLimit() {
		return vipLv;
	}

	@Override
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public void setEndTime(long endTime) {
		this.endTime = startTime;
	}
}
