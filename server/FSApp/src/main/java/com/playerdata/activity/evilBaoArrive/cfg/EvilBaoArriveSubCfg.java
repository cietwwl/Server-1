package com.playerdata.activity.evilBaoArrive.cfg;
import com.common.BaseConfig;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;

public class EvilBaoArriveSubCfg extends BaseConfig implements ActivitySubCfgIF{
	private int id; //活动子id
	private int parentCfg; //所属活动id
	private int awardCount; //需要达到次数
	private String awardGift; //礼包ID 

	public int getId() {
		return id;
	}
	
 	public int getParentCfg() {
 		return parentCfg;
 	}
 	
 	public int getAwardCount() {
 		return awardCount;
 	}
 	
 	public String getAwardGift() {
 		return awardGift;
 	}

	@Override
	public int getDay() {
		return 1;
	}

	@Override
	public int getType() {
		return parentCfg;
	}

	@Override
	public void setCfgReward(String reward) {
		this.awardGift = reward;
	}
}
