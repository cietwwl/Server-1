package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;

public class GmOnlineLimitModify implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		int limit = GmUtils.parseInt(request.getArgs(), "value");
		if(limit > 0){//开启
			ServerStatusMgr.setOnlineLimit(limit);
		}

		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", 0);
		response.addResult(resultMap );
		return response;
	}
	

}
