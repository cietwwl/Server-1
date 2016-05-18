package com.gm.task;

import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;

public class GmResponsePlayerQuestion implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		Map<String, Object> args = request.getArgs();
		int id = GmUtils.parseInt(args, "id");
		int serverId = GmUtils.parseInt(args, "serverId");
		String roleId = GmUtils.parseString(args, "roleId");
		String content = GmUtils.parseString(args, "content");
		
		response.setStatus(0);
		response.setCount(1);
		return response;
	}

}
