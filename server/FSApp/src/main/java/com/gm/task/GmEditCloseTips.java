package com.gm.task;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.rw.manager.GameManager;
import com.rw.service.http.platformResponse.ServerCloseTipsBaseDataResponse;
import com.rw.service.platformService.PlatformService;

public class GmEditCloseTips implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try{
			Map<String, Object> args = request.getArgs();
			String tips = GmUtils.parseString(args, "tips");
			
			if (StringUtils.isBlank(tips)) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}

			ServerCloseTipsBaseDataResponse serverCloseTipsBaseDataResponse = new ServerCloseTipsBaseDataResponse();
			serverCloseTipsBaseDataResponse.setTips(tips);
			serverCloseTipsBaseDataResponse.setZoneId(GameManager.getZoneId());
			
			
			PlatformService.SendResponse("com.rw.netty.http.requestHandler.ServerCloseTipsHandler", "updateServerCloseTips", serverCloseTipsBaseDataResponse, ServerCloseTipsBaseDataResponse.class);
			
			response.setStatus(0);
			response.setCount(1);
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
