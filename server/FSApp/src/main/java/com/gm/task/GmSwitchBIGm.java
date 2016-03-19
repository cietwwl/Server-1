package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.rw.service.gm.GMHandler;

public class GmSwitchBIGm implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		
		int status = (Integer)request.getArgs().get("value");
		if(status == 0){//开启
			GMHandler.getInstance().setActive(true);
		}else{ //关闭
			GMHandler.getInstance().setActive(false);
		}

		response.setStatus(0);
		response.setCount(1);
		
		
		response.addResult(resultMap );
		return response;
	}
	

}
