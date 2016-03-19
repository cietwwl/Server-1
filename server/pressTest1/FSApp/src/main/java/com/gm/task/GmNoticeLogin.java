package com.gm.task;

import com.gm.GmRequest;
import com.gm.GmResponse;

public class GmNoticeLogin implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		String title = (String)request.getArgs().get("title");
		String content = (String)request.getArgs().get("content");
		long startTime = (Long)request.getArgs().get("startTime");
		long endTime = (Long)request.getArgs().get("endTime");
		
		
	
		response.setStatus(0);
		response.setCount(1);
		return response;
	}
	

}
