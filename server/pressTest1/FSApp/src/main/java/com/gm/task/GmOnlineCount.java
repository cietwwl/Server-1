package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.rw.netty.UserChannelMgr;

public class GmOnlineCount implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		int onlineCount = UserChannelMgr.getCount();

		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", onlineCount);
		response.addResult(resultMap );
		return response;
	}
	

}
