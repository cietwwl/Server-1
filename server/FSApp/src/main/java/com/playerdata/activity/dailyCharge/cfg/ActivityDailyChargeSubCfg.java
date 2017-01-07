package com.playerdata.activity.dailyCharge.cfg;
import com.common.BaseConfig;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;

public class ActivityDailyChargeSubCfg extends BaseConfig implements ActivitySubCfgIF{
	private int id; //活动id
	private int type; //父id
	private String emailTitle; //标题
	private int goToType; //活动跳转类型
	private int count; //领取条件
	private String giftId; //奖励礼包
	private String title; //活动子标题
	private int day; //激活时间

	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
	
	public String getEmailTitle() {
		return emailTitle;
	}
	
	public int getGoToType() {
		return goToType;
	}
	
	public int getCount() {
		return count;
	}
	
	public String getGiftId() {
	  return giftId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getDay() {
		return day;
	}

	@Override
	public void setCfgReward(String reward) {
		this.giftId = reward;
	}
}
