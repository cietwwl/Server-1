package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.PlayerMgr;

public class GmCheckDataOpProgress implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		int opProgress = PlayerMgr.getInstance().getOPProgress();
		

		if(opProgress == 0 ){
			opProgress = 100;
		}
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", opProgress);
		response.addResult(resultMap );
		return response;
	}
	

}
