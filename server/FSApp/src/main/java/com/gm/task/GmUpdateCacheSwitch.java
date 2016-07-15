package com.gm.task;

import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.rw.fsutil.dao.cache.CacheLoggerSwitch;

public class GmUpdateCacheSwitch implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();

		response.setStatus(0);
		response.setCount(1);
		
		Map<String, Object> args = request.getArgs();
		boolean value = GmUtils.parseBoolean(args, "value");
		CacheLoggerSwitch.getInstance().setCacheLoggerSwitch(value);

		return response;
	}

}
