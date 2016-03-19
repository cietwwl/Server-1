package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.log.GameLog;
import com.log.LogModule;

public class GmForClassLoad implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		
		String className = (String)request.getArgs().get("className");
		
		Class clazz = null;
		
		try {
			clazz = Class.forName(className);
			if(clazz!=null){
				Object obj = clazz.newInstance();
				Runnable task = (Runnable)obj;
				task.run();
			}
			response.setStatus(0);
		} catch (Exception e) {
			response.setStatus(1);
			GameLog.error(LogModule.GM.getName(), "GmForClassLoad", "GmForClassLoad[doTask]", e);
		}
		
		
		response.setCount(1);
		
		
		response.addResult(resultMap );
		return response;
	}
	

}
