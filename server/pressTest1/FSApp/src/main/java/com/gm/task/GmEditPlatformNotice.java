package com.gm.task;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.SocketHelper;
import com.rw.service.http.HttpServer;
import com.rw.service.http.platformResponse.PlatformNoticeBaseDataResponse;

public class GmEditPlatformNotice implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try{
			String title = (String) request.getArgs().get("title");
			String content = (String)request.getArgs().get("content");
			long startTime = Long.parseLong(request.getArgs().get("startTime").toString());
			long endTime = Long.parseLong(request.getArgs().get("endTime").toString());
			
			if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}

			
			PlatformNoticeBaseDataResponse platformNoticeBaseDataResponse = new PlatformNoticeBaseDataResponse();
			platformNoticeBaseDataResponse.setTitle(title);
			platformNoticeBaseDataResponse.setContent(content);
			platformNoticeBaseDataResponse.setStartTime(startTime);
			platformNoticeBaseDataResponse.setEndTime(endTime);
			
			HttpServer.SendResponse("com.rw.netty.http.requestHandler.PlatformNoticeHandler", "updatePlatformNotice", platformNoticeBaseDataResponse, PlatformNoticeBaseDataResponse.class);
			
			response.setStatus(0);
			response.setCount(1);
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
