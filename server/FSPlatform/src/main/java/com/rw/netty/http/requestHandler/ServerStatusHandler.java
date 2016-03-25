package com.rw.netty.http.requestHandler;

import com.rw.account.ZoneInfoCache;
import com.rw.platform.PlatformFactory;
import com.rw.platform.PlatformService;
import com.rw.service.http.platformResponse.ServerBaseDataResponse;
import com.rw.service.http.request.ResponseObject;
import com.rwproto.PlatformGSMsg.eServerStatusType;

public class ServerStatusHandler {
	public static ResponseObject notifyServerData(ServerBaseDataResponse serverBaseDataResponse){
		
		int zoneId = serverBaseDataResponse.getZoneId();
		int onlineNum = serverBaseDataResponse.getOnlineNum();
		int status = serverBaseDataResponse.getStatus();
		
		ZoneInfoCache zoneInfo = PlatformFactory.getPlatformService().getZoneInfo(zoneId);
		if(zoneInfo == null){
			PlatformFactory.getPlatformService().initZoneCache();
		}
		
		refreshZoneInfo(zoneInfo, onlineNum, status);
		ResponseObject result = new ResponseObject();
		result.setSuccess(true);
		return result;
	}
	
	public static void refreshZoneInfo(ZoneInfoCache zoneInfo, int onlineNum, int status){
		if(zoneInfo == null){
			return;
		}
		zoneInfo.setOnlineNum(onlineNum);
		zoneInfo.setStatus(status);
		zoneInfo.setNotifyTime(System.currentTimeMillis());
	}
}
