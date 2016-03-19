package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.bm.arena.RobotManager;
import com.gm.GmExecutor;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.log.GameLog;
import com.log.LogModule;

public class GmStartRobotCreation implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		GmExecutor.getInstance().submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					RobotManager.getInstance().createRobots();
				} catch (Throwable e) {
					GameLog.error(LogModule.GM.getName(), "GmStartRobotCreation", "GmStartRobotCreation[doTask] GmExecutor run", e);
				}
			}
		});
		
		
		
		response.addResult(resultMap );
		return response;
	}
	

}
