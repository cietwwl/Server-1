package com.playerdata.activity.growthFund.cfg;
import com.playerdata.activity.growthFund.GrowthFundType;
import com.playerdata.activityCommon.ActivityTimeHelper;
import com.playerdata.activityCommon.ActivityTimeHelper.TimePair;
import com.playerdata.activityCommon.activityType.ActivityCfgIF;

public class GrowthFundBasicCfg implements ActivityCfgIF{
	
	private int key; //关键字
	private int vipLv; //可购买的VIP等级
	private int price; //价格
	private int levelLimit;	//等级限制
	private int type; // 成长基金类型
	private GrowthFundType _fundType; // 类型的枚举形式
	private long startTime = 0;
	private long endTime = Long.MAX_VALUE;
	private String startTimeStr = "197012120500";
	private String endTimeStr = "300012120500";
	
	private String titleBG;		//活动的描述
	private int isSynDesc = 0;	//是否服务端同步描述
	
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
	public void setVersion(int version) {
		//TODO 以后要加上
	}
	
	@Override
	public int getLevelLimit() {
		return levelLimit;
	}

	@Override
	public boolean isDailyRefresh() {
		return false;
	}
	
	@Override
	public boolean isEveryDaySame() {
		return false;
	}

	public int getType() {
		return type;
	}
	
	public GrowthFundType getFundType() {
		return _fundType;
	}
	
	public void ExtraInitAfterLoad() {
		this._fundType = GrowthFundType.getBySign(type);
	}

	@Override
	public int getVipLimit() {
		return vipLv;
	}
	
	@Override
	public void setStartAndEndTime(String startTimeStr, String endTimeStr) {
		TimePair timePair = ActivityTimeHelper.transToAbsoluteTime(startTimeStr, endTimeStr);
		if(null == timePair) return;
		startTime = timePair.getStartMil();
		endTime = timePair.getEndMil();
		this.startTimeStr = timePair.getStartTime();
		this.endTimeStr = timePair.getEndTime();
	}

	@Override
	public String getStartTimeStr() {
		return startTimeStr;
	}

	@Override
	public String getEndTimeStr() {
		return endTimeStr;
	}
	
	@Override
	public String getActDesc() {
		if(0 != isSynDesc){
			return titleBG;
		}
		return null;
	}
	
	@Override
	public void setActDesc(String actDesc) {
		titleBG = actDesc;
	}
}
