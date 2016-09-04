package com.gm.task;

import java.util.Map;

import com.bm.serverStatus.ServerStatusMgr;
import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;

public class GmRemoveGmNotice implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try{
			response.setStatus(0);
			response.setCount(1);
			Map<String, Object> args = request.getArgs();
			int gmNoticeId = GmUtils.parseInt(args, "noticeId");
			ServerStatusMgr.removeGmNotice(gmNoticeId);
			
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
