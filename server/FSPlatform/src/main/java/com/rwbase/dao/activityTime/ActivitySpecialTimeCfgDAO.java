package com.rwbase.dao.activityTime;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bm.login.ZoneBM;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.http.requestHandler.ServerStatusHandler;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.zone.TableZoneInfo;
import com.rwproto.PlatformGSMsg.ActCfgInfo;

//	<bean class="com.rwbase.dao.activityTime.ActivitySpecialTimeCfgDAO"  init-method="init" />

public class ActivitySpecialTimeCfgDAO extends CfgCsvDao<ActivitySpecialTimeCfg> {
	
	private static long LAST_MODIFY_TIME = 0;
	private static boolean OPENED_TIMER = false;
	private HashMap<Integer, List<ActCfgInfo>> actCfgMap = new HashMap<Integer, List<ActCfgInfo>>();
	
	public static ActivitySpecialTimeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivitySpecialTimeCfgDAO.class);
	}

	@Override
	public Map<String, ActivitySpecialTimeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivitySpecialTimeCfg.csv",ActivitySpecialTimeCfg.class);
		for(ActivitySpecialTimeCfg cfgTmp : cfgCacheMap.values()){
			decodeActivityZone(cfgTmp);
		}
		if(!OPENED_TIMER){
			OPENED_TIMER = true;
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			service.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					try {
						refreshActivtiyTimeCfg();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				
			}, 0, 10, TimeUnit.SECONDS);
		}
		return cfgCacheMap;
	}
	
	public void refreshActivtiyTimeCfg(){
		File cfgFile = new File(CfgCsvHelper.class.getResource("/config/Activity/ActivitySpecialTimeCfg.csv").getFile());
		if(0 == LAST_MODIFY_TIME){
			LAST_MODIFY_TIME = cfgFile.lastModified();
			return;
		}
		if(LAST_MODIFY_TIME != cfgFile.lastModified()){
			reload();
			List<TableZoneInfo> zoneList = ZoneBM.getInstance().getAllZoneCfg();
			for(TableZoneInfo zone : zoneList){
				if(zone.getStatus() >= 0){
					ServerStatusHandler.sendActivityTimeData(zone.getId());
				}
			}
			LAST_MODIFY_TIME = cfgFile.lastModified();
		}
	}
	
	public List<ActCfgInfo> getZoneAct(int zoneId){
		return actCfgMap.get(zoneId);
	}
	
	private void decodeActivityZone(ActivitySpecialTimeCfg timeCfg){
		String[] zoneSections = timeCfg.getZoneId().split(",");
		for(String section : zoneSections){
			int startZoneId = 0;
			int endZoneId = 0;
			String[] startAndEnd = section.split("_");
			if(2 == startAndEnd.length){
				startZoneId = Integer.parseInt(startAndEnd[0]);
				endZoneId = Integer.parseInt(startAndEnd[1]);
			}else if(1 == startAndEnd.length){
				startZoneId = Integer.parseInt(startAndEnd[0]);
				endZoneId = startZoneId;
			}else{
				System.out.println("ActivitySpecialTimeCfgDAO-decodeActivityZone:" + "id[" + timeCfg.getId() + "]区号区间填写有误");
				continue;
			}
			for(int i = startZoneId; i <= endZoneId; i++){
				List<ActCfgInfo> cfgList = actCfgMap.get(i);
				if(null == cfgList){
					cfgList = new ArrayList<ActCfgInfo>();
					actCfgMap.put(i, cfgList);
				}
				cfgList.add(buildActCfgInfo(timeCfg));
			}
		}
	}

	private ActCfgInfo buildActCfgInfo(ActivitySpecialTimeCfg specialTimecfg){
		ActCfgInfo.Builder cfgInfo = ActCfgInfo.newBuilder();
		cfgInfo.setCfgId(specialTimecfg.getCfgId());
		cfgInfo.setActDesc(specialTimecfg.getActDesc());
		cfgInfo.setStartTime(specialTimecfg.getStartTime());
		cfgInfo.setEndTime(specialTimecfg.getEndTime());
		cfgInfo.setStartViceTime(specialTimecfg.getStartViceTime());
		cfgInfo.setEndViceTime(specialTimecfg.getEndViceTime());
		cfgInfo.setRangeTime(specialTimecfg.getRangeTime());
		return cfgInfo.build();
	}
}
