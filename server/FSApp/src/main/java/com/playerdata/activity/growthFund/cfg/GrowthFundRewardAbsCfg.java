package com.playerdata.activity.growthFund.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.common.BaseConfig;
import com.playerdata.activityCommon.activityType.ActivitySubCfgIF;
import com.rwbase.dao.copy.pojo.ItemInfo;

public abstract class GrowthFundRewardAbsCfg extends BaseConfig implements ActivitySubCfgIF {

	private int key; //关键字
	private String rewardContents; //奖励内容
	private int type;	//父类型
	private List<ItemInfo> rewardItemInfos;
	
	public int getKey() {
		return key;
	}
	
	public String getRewardContents() {
		return rewardContents;
	}
	
	@Override
	public int getId() {
		return key;
	}
	
	@Override
	public int getDay() {
		return 1;
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	public List<ItemInfo> getRewardItemInfos() {
		return rewardItemInfos;
	}
	
	public abstract int getRequiredCondition();
	
	@Override
	public void ExtraInitAfterLoad() {
		String[] allRewards = rewardContents.split(",");
		List<ItemInfo> tempList = new ArrayList<ItemInfo>(allRewards.length);
		for (int i = 0; i < allRewards.length; i++) {
			String[] singleReward = allRewards[i].split("_");
			tempList.add(new ItemInfo(Integer.parseInt(singleReward[0]), Integer.parseInt(singleReward[1])));
		}
		rewardItemInfos = Collections.unmodifiableList(tempList);
	}
	
	@Override
	public void setCfgReward(String reward) {
		this.rewardContents = reward;
		ExtraInitAfterLoad();
	}
}
