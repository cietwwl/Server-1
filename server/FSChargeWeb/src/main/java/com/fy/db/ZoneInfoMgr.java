package com.fy.db;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fy.common.SpringContextUtil;


public class ZoneInfoMgr {

	
	private Map<Integer,ZoneInfo> zoneMap = new ConcurrentHashMap<Integer,ZoneInfo>();
	
	private ScheduledExecutorService refreshService;
	
	public static ZoneInfoMgr getInstance(){
		
		return SpringContextUtil.getBean("zoneInfoMgr");
	
	}
	
	public void init(){
		
		refreshService = Executors.newScheduledThreadPool(1);
		refresh();
		refreshService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {
					refresh();
				} catch (Throwable e) {
					// TODO: do nothing
					System.out.println("ZoneInfoMgr[refresh] error:"+e.getMessage());
				}
				
			}
		}, 10, 10, TimeUnit.SECONDS);		
		
		
	}

	private void refresh() {
		Map<Integer,ZoneInfo> zoneMapTmp = new ConcurrentHashMap<Integer, ZoneInfo>();
		List<ZoneInfo> fromDb = fromDb();

		for (ZoneInfo zoneInfo : fromDb) {
			zoneMapTmp.put(zoneInfo.getZoneId(), zoneInfo);
		}	
		
		zoneMap = zoneMapTmp;
	}
	
	private List<ZoneInfo> fromDb(){
		final String sql = "SELECT zoneId,serverIp,chargePort FROM mt_zone_info;";		
		List<ZoneInfo> zoneList = ChargeDbMgr.getInstance().query(sql, new Object[]{}, ZoneInfo.class);
		return zoneList;
		
	}
	
	
	public ZoneInfo getZone(int zoneId){
		return zoneMap.get(zoneId);
	}
	
	
}
