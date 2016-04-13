package com.gm.task;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.rw.service.http.HttpServer;
import com.rw.service.http.platformResponse.PlatformNoticeBaseDataResponse;
import com.rw.service.platformService.PlatformService;

public class GmEditPlatformNotice implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try{
			Map<String, Object> args = request.getArgs();
			String title = GmUtils.parseString(args, "title");;
			String content = GmUtils.parseString(args, "content");
			long startTime = GmUtils.parseLong(args, "startTime");
			long endTime = GmUtils.parseLong(args, "endTime");
			
			if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			
			if(title.length() > 30){
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_INVALID_NOTICE_HEAD.getStatus()));
			}

			content = content.replace("<br>", "*");
			PlatformNoticeBaseDataResponse platformNoticeBaseDataResponse = new PlatformNoticeBaseDataResponse();
			platformNoticeBaseDataResponse.setTitle(title);
			platformNoticeBaseDataResponse.setContent(content);
			platformNoticeBaseDataResponse.setStartTime(startTime);
			platformNoticeBaseDataResponse.setEndTime(endTime);
			
			PlatformService.SendResponse("com.rw.netty.http.requestHandler.PlatformNoticeHandler", "updatePlatformNotice", platformNoticeBaseDataResponse, PlatformNoticeBaseDataResponse.class);
			
			response.setStatus(0);
			response.setCount(1);
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
