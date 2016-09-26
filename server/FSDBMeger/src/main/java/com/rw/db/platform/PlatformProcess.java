package com.rw.db.platform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.db.DBInfo;
import com.rw.db.PlatformDBInfo;
import com.rw.db.dao.DBMgr;
import com.rw.log.DBLog;

/**
 * 更新平台信息
 * @author lida
 *
 */
public class PlatformProcess {
	public void exec(PlatformDBInfo platformDBInfo, DBInfo backup_oriDBInfo, DBInfo backup_tarDBInfo){
		int oriZoneId = backup_oriDBInfo.getZoneId();
		int tarZoneId = backup_tarDBInfo.getZoneId();
		String sql1 = "select * from mt_zone_info where zoneId = "+oriZoneId;
		String sql2 = "select * from mt_zone_info where zoneId = "+tarZoneId;
		
		List<TableZoneInfo> query1 = DBMgr.getInstance().query(platformDBInfo.getDBName(), sql1, new Object[]{}, TableZoneInfo.class);
		List<TableZoneInfo> query2 = DBMgr.getInstance().query(platformDBInfo.getDBName(), sql2, new Object[]{}, TableZoneInfo.class);
		
		
		if(query1 == null || query2 == null || query1.size() <= 0 || query2.size() <= 0){
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
		tarZoneInfo.setSubZone(oriZoneInfo.getZoneId());
		tarZoneInfo.setDb_name(backup_oriDBInfo.getDBName());
		Map<String, TableZoneInfo> map = new HashMap<String, TableZoneInfo>();
		map.put(String.valueOf(tarZoneInfo.getId()), tarZoneInfo);
		DBMgr.getInstance().update(platformDBInfo.getDBName(), map, TableZoneInfo.class);
		
		oriZoneInfo.setDb_name(backup_oriDBInfo.getDBName());
		Map<String, TableZoneInfo> orimap = new HashMap<String, TableZoneInfo>();
		orimap.put(String.valueOf(oriZoneInfo.getId()), oriZoneInfo);
		DBMgr.getInstance().update(platformDBInfo.getDBName(), orimap, TableZoneInfo.class);
	}
}
