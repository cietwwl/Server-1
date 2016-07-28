package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.task.gmCommand.GmCommandManager;
import com.gm.util.GmUtils;

public class GmExecuteGM implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		response.setStatus(0);
		response.setCount(1);
		
		Map<String, Object> args = request.getArgs();
		String param = GmUtils.parseString(args, "command");
		String result = GmCommandManager.executeGMCommand(param);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("resultMsg", result);
		
		response.addResult(resultMap);
		
		return response;
	}

}
