package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.bm.serverStatus.ServerStatus;
import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.rw.manager.GameManager;
import com.rw.netty.ServerConfig;

public class GmServerInfo implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		String status = (String)request.getArgs().get("value");
		

		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(StringUtils.equals("all", status)){//开启
			resultMap.put("status", 0);
			resultMap.put("ip", ServerConfig.getInstance().getServeZoneInfo().getServerIp());
			resultMap.put("port", ServerConfig.getInstance().getServeZoneInfo().getPort());
			resultMap.put("servertype", "game");
			resultMap.put("id", GameManager.getServerId());
			
		}
		response.addResult(resultMap );
		return response;
	}
	

}
