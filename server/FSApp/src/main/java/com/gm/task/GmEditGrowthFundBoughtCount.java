package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.activity.growthFund.GrowthFundGlobalData;
import com.playerdata.activity.growthFund.data.ActivityGrowthFundItemHolder;

public class GmEditGrowthFundBoughtCount implements IGmTask {
	
	private static final String key = "BoughtCount";

	@Override
	public GmResponse doTask(GmRequest request) {
		Object obj = request.getArgs().get(key);
		GmResponse resp = new GmResponse();
		if (obj != null) {
			Integer value = (Integer) obj;
			GrowthFundGlobalData globalData = ActivityGrowthFundItemHolder.getInstance().getGlobalData();
			globalData.setAlreadyBoughtCount(value);
			resp.setStatus(0);
			resp.setCount(1);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put(key, globalData.getAlreadyBoughtCount());
			resp.addResult(resultMap);
		} else {
			resp.setStatus(-1);
		}
		return resp;
	}

}
