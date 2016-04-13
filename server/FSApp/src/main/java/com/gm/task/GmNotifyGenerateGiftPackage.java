package com.gm.task;

import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;

public class GmNotifyGenerateGiftPackage implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> args = request.getArgs();
		int id = GmUtils.parseInt(args, "id");
		int type = GmUtils.parseInt(args, "type");
		
		return response;
	}

}
