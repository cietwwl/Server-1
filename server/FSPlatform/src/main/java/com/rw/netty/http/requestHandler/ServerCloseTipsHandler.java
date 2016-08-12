package com.rw.netty.http.requestHandler;

import com.rw.account.ZoneInfoCache;
import com.rw.platform.PlatformFactory;
import com.rw.service.http.platformResponse.ServerCloseTipsBaseDataResponse;
import com.rw.service.http.request.ResponseObject;

public class ServerCloseTipsHandler {
	public static ResponseObject updateServerCloseTips(ServerCloseTipsBaseDataResponse response){
		
		int zoneId = response.getZoneId();
		String tips = response.getTips();
		
		ZoneInfoCache zoneInfo = PlatformFactory.getPlatformService().getZoneInfo(zoneId);
		zoneInfo.setCloseTips(tips);
		zoneInfo.update();
		ResponseObject result = new ResponseObject();
		result.setSuccess(true);
		return result;
	}
}
