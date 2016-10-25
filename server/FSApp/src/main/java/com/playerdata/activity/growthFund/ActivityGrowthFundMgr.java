package com.playerdata.activity.growthFund;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeSubCfgDAO;
import com.playerdata.activity.dailyCharge.data.ActivityDailyRechargeTypeSubItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItem;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItemHolder;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundSubItem;
import com.playerdata.activityCommon.AbstractActivityMgr;
import com.playerdata.activityCommon.UserActivityChecker;

public class ActivityGrowthFundMgr extends AbstractActivityMgr<ActivityGrowthFundItem> {

	private static ActivityGrowthFundMgr instance = new ActivityGrowthFundMgr();

	public static ActivityGrowthFundMgr getInstance() {
		return instance;
	}
	
	@Override
	protected List<String> checkRedPoint(ActivityGrowthFundItem item) {
		List<String> redPointList = new ArrayList<String>();
		List<ActivityGrowthFundSubItem> subItems = (List<ActivityGrowthFundSubItem>) item.getSubItemList();
		for (ActivityGrowthFundSubItem subItem : subItems) {
			if (canGetReward(subItem) || !item.isHasViewed()) {
				redPointList.add(String.valueOf(item.getCfgId()));
				break;
			}
		}
		return redPointList;
	}
	
	protected UserActivityChecker<ActivityGrowthFundItem> getHolder(){
		return ActivityGrowthFundItemHolder.getInstance();
	}
	
	protected boolean isThisActivityIndex(int index){
		return index < 150000 && index > 140000;
	}
	
	private boolean canGetReward(ActivityGrowthFundSubItem subItem){
		return ?? && !subItem.isGet();
	}
}
