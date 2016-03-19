package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import com.bm.serverStatus.ServerStatus;
import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.PlayerMgr;

public class GmServerSwitch implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		
		int status = (Integer)request.getArgs().get("value");
		if(status == 0){//开启
			open();
		}else{ //关闭
			close();
		}

		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("value", 0);
		response.addResult(resultMap );
		return response;
	}
	
	private void open(){
		ServerStatusMgr.setStatus(ServerStatus.OPEN);
	}
	private void close(){
		ServerStatusMgr.setStatus(ServerStatus.CLOSE);
	}

}
