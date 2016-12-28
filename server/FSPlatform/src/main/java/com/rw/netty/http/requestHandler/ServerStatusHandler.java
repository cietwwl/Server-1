package com.rw.netty.http.requestHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.account.ZoneInfoCache;
import com.rw.platform.PlatformFactory;
import com.rw.service.http.platformResponse.ServerBaseDataResponse;
import com.rw.service.http.request.ResponseObject;
import com.rwbase.dao.activityTime.ActivitySpecialTimeCfgDAO;
import com.rwproto.MsgDef.Command;
import com.rwproto.PlatformGSMsg.ActCfgInfo;
import com.rwproto.PlatformGSMsg.ActivityTimeInfo;

public class ServerStatusHandler {
	
	public static ResponseObject notifyServerData(ServerBaseDataResponse serverBaseDataResponse){
		
		int zoneId = serverBaseDataResponse.getZoneId();
		int onlineNum = serverBaseDataResponse.getOnlineNum();
		int status = serverBaseDataResponse.getStatus();
		
		ResponseObject result = new ResponseObject();
		
		Map<Integer, ZoneInfoCache> map = PlatformFactory.getPlatformService().getZoneInfoBySubZoneId(zoneId);
		if (map == null || map.size() <= 0) {
			PlatformFactory.getPlatformService().initZoneCache();
			result.setSuccess(false);
			return result;
		}
		
		ZoneInfoCache targetZoneInfo = map.get(zoneId);
		if(null != targetZoneInfo){
			if(status == 5 || (targetZoneInfo.getStatus() < 0 && status >= 0)){
				// 有新启动的服务器，把和它相关的活动数据发过去
				sendActivityTimeData(zoneId);
			}
		}
		
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
	
	public static void sendActivityTimeData(int zoneId) {
		ActivityTimeInfo.Builder actBuilder = ActivityTimeInfo.newBuilder();
		List<ActCfgInfo> cfgs = ActivitySpecialTimeCfgDAO.getInstance().getZoneAct(zoneId);
		if(cfgs == null) {
			cfgs = new ArrayList<ActCfgInfo>();
		}
		actBuilder.addAllActInfos(cfgs);
		ZoneInfoCache zone = PlatformFactory.getPlatformService().getZoneInfo(zoneId);
		try{
			PlatformFactory.clientManager.submitReqeust(zone.getServerIp(), Integer.parseInt(zone.getPort()), actBuilder.build().toByteString(), Command.MSG_ACTIVITY_TIME, "");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
