package com.rw.netty.http.requestHandler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.account.ZoneInfoCache;
import com.rw.platform.PlatformFactory;
import com.rw.service.http.platformResponse.ServerBaseDataResponse;
import com.rw.service.http.request.ResponseObject;
import com.rwbase.dao.activityTime.ActivitySpecialTimeCfgDAO;

public class ServerStatusHandler {
	
	public static ResponseObject notifyServerData(ServerBaseDataResponse serverBaseDataResponse){
		
		int zoneId = serverBaseDataResponse.getZoneId();
		int onlineNum = serverBaseDataResponse.getOnlineNum();
		int status = serverBaseDataResponse.getStatus();
		int actTimeVersion = serverBaseDataResponse.getActivityTimeVersion();
		
		ResponseObject result = new ResponseObject();
		
		Map<Integer, ZoneInfoCache> map = PlatformFactory.getPlatformService().getZoneInfoBySubZoneId(zoneId);
		if (map == null || map.size() <= 0) {
			PlatformFactory.getPlatformService().initZoneCache();
			result.setSuccess(false);
			return result;
		}
		//检查活动时间的变化
		result.setActTimeInfo(ActivitySpecialTimeCfgDAO.getInstance().getZoneAct(zoneId, actTimeVersion));
		for (Iterator<Entry<Integer, ZoneInfoCache>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, ZoneInfoCache> entry = iterator.next();
			ZoneInfoCache zoneInfo = entry.getValue();
			refreshZoneInfo(zoneInfo, onlineNum, status);
		}
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
