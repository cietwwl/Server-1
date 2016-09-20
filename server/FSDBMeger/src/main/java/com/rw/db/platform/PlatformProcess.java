package com.rw.db.platform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.db.PlatformDBInfo;
import com.rw.db.dao.DBMgr;
import com.rw.log.DBLog;

/**
 * 更新平台信息
 * @author lida
 *
 */
public class PlatformProcess {
	public void exec(PlatformDBInfo platformDBInfo, int oriZoneId, int tarZoneId){
		String sql1 = "select * from mt_zone_info where zoneId = "+oriZoneId;
		String sql2 = "select * from mt_zone_info where zoneId = "+tarZoneId;
		
		List<TableZoneInfo> query1 = DBMgr.getInstance().query(platformDBInfo.getDBName(), sql1, new Object[]{}, TableZoneInfo.class);
		List<TableZoneInfo> query2 = DBMgr.getInstance().query(platformDBInfo.getDBName(), sql2, new Object[]{}, TableZoneInfo.class);
		
		
		if(query1 == null || query1 == null){
			DBLog.LogError("PlatformProcess", "can not find the zone info");
			return;
		}
		
		TableZoneInfo oriZoneInfo = query1.get(0);
		
		TableZoneInfo tarZoneInfo = query2.get(0);
		
		
		if(oriZoneInfo == null || tarZoneInfo == null){
			DBLog.LogError("PlatformProcess", "can not find the zone info");
			return;
		}
		
		tarZoneInfo.setServerIp(oriZoneInfo.getServerIp());
		tarZoneInfo.setIntranetIp(oriZoneInfo.getIntranetIp());
		tarZoneInfo.setPort(oriZoneInfo.getPort());
		tarZoneInfo.setSubZone(tarZoneInfo.getZoneId());
		tarZoneInfo.setSubZone(1);		
		Map<String, TableZoneInfo> map = new HashMap<String, TableZoneInfo>();
		map.put(String.valueOf(tarZoneId), tarZoneInfo);
		DBMgr.getInstance().update(platformDBInfo.getDBName(), map, TableZoneInfo.class);
		
	}
}
