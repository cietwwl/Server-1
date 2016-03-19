package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;

public class GmChargeSwitch implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		int status = (Integer)request.getArgs().get("value");
		if(status == 0){//开启
			ServerStatusMgr.setChargeOn(true);
		}else{ //关闭
			ServerStatusMgr.setChargeOn(false);
		}

		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", 0);
		response.addResult(resultMap );
		return response;
	}

}
