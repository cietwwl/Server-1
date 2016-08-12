package com.gm.task.gmCommand;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.rw.manager.GameManager;
import com.rw.service.http.platformResponse.ServerCloseTipsBaseDataResponse;
import com.rw.service.platformService.PlatformService;

@GmCommand
public class GmServerStatusTips implements IGmCommand{

	@Override
	public String executeGMCommand(String param) {
		// TODO Auto-generated method stub
		String[] split = param.split("#");
		String tips = split[0];
		
		if (StringUtils.isBlank(tips)) {
			return String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus());
		}

		ServerCloseTipsBaseDataResponse serverCloseTipsBaseDataResponse = new ServerCloseTipsBaseDataResponse();
		serverCloseTipsBaseDataResponse.setTips(tips);
		serverCloseTipsBaseDataResponse.setZoneId(GameManager.getZoneId());
		
		
		PlatformService.SendResponse("com.rw.netty.http.requestHandler.ServerCloseTipsHandler", "updateServerCloseTips", serverCloseTipsBaseDataResponse, ServerCloseTipsBaseDataResponse.class);
		
		return "success";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ServerStatusTips";
	}

}
