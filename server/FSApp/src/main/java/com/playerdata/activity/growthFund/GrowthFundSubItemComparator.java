package com.playerdata.activity.growthFund;

import java.util.Comparator;

import com.playerdata.activity.growthFund.data.ActivityGrowthFundSubItem;

public class GrowthFundSubItemComparator implements Comparator<ActivityGrowthFundSubItem> {

	@Override
	public int compare(ActivityGrowthFundSubItem o1, ActivityGrowthFundSubItem o2) {
		return o1.getRequiredCondition() < o2.getRequiredCondition() ? -1 : 1;
	}

}
