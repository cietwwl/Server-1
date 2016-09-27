package com.rw.netty.http.requestHandler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.account.ZoneInfoCache;
import com.rw.platform.PlatformFactory;
import com.rw.service.http.platformResponse.ServerCloseTipsBaseDataResponse;
import com.rw.service.http.request.ResponseObject;

public class ServerCloseTipsHandler {
	public static ResponseObject updateServerCloseTips(ServerCloseTipsBaseDataResponse response){
		
		int zoneId = response.getZoneId();
		String tips = response.getTips();

		ResponseObject result = new ResponseObject();
		Map<Integer, ZoneInfoCache> map = PlatformFactory.getPlatformService().getZoneInfoBySubZoneId(zoneId);
		for (Iterator<Entry<Integer, ZoneInfoCache>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, ZoneInfoCache> entry = iterator.next();
			ZoneInfoCache zoneInfo = entry.getValue();
			zoneInfo.setCloseTips(tips);
			zoneInfo.update();

		}
		result.setSuccess(true);
		return result;
	}
}
