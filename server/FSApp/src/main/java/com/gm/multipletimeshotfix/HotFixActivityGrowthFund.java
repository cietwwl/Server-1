package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import com.playerdata.activity.growthFund.data.ActivityGrowthFundItemHolder;

public class HotFixActivityGrowthFund implements Callable<Void>{

	@Override
	public Void call() throws Exception {	
		Field instanceField = ActivityGrowthFundItemHolder.class.getDeclaredField("instance");
		instanceField.setAccessible(true);
		instanceField.set(ActivityGrowthFundItemHolder.getInstance(), new ActivityGrowthFundItemHolderHotfix());
		return null;
	}

}
