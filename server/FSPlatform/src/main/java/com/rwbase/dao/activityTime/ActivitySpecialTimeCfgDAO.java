package com.rwbase.dao.activityTime;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rwbase.dao.activityTime.ActivitySpecialTimeCfgDAO"  init-method="init" />

public class ActivitySpecialTimeCfgDAO extends CfgCsvDao<ActivitySpecialTimeCfg> {
	
	private static long LAST_MODIFY_TIME = 0;
	private static AtomicInteger platformVersion = new AtomicInteger(0);
	private static boolean OPENED_TIMER = false;
	private HashMap<Integer, List<SingleActTime>> actCfgMap = new HashMap<Integer, List<SingleActTime>>();
	private HashMap<Integer, List<SingleActTime>> actCfgTmpMap = new HashMap<Integer, List<SingleActTime>>();
	
	public static ActivitySpecialTimeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivitySpecialTimeCfgDAO.class);
	}

	@Override
	public Map<String, ActivitySpecialTimeCfg> initJsonCfg() {
		int tmpVersion = -1;
		Map<String, ActivitySpecialTimeCfg> tmpMap = CfgCsvHelper.readCsv2Map("Activity/ActivitySpecialTimeCfg.csv",ActivitySpecialTimeCfg.class);
		actCfgTmpMap = new HashMap<Integer, List<SingleActTime>>();
		for(ActivitySpecialTimeCfg cfgTmp : tmpMap.values()){
			decodeActivityZone(cfgTmp);
			if(cfgTmp.getId() == 1) {
				tmpVersion = cfgTmp.getVersion();
			}
		}
		if(!OPENED_TIMER){
			//第一次执行时，启动定时器
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
		cfgCacheMap = tmpMap;
		actCfgMap = actCfgTmpMap;
		if(-1 != tmpVersion){
			platformVersion.set(tmpVersion);
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
			LAST_MODIFY_TIME = cfgFile.lastModified();
		}
	}
	
	public ActCfgInfo getZoneAct(int zoneId, int version){
		ActCfgInfo actInfo = new ActCfgInfo();
		int thisVersion = platformVersion.get();
		actInfo.setPlatformVersion(thisVersion);
		if(version < thisVersion){
			actInfo.setActList(actCfgMap.get(zoneId));
		}
		return actInfo;
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
				List<SingleActTime> cfgList = actCfgTmpMap.get(i);
				if(null == cfgList){
					cfgList = new ArrayList<SingleActTime>();
					actCfgTmpMap.put(i, cfgList);
				}
				cfgList.add(buildSingleActTime(timeCfg));
			}
		}
	}
	
	private SingleActTime buildSingleActTime(ActivitySpecialTimeCfg specialTimecfg){
		SingleActTime cfgInfo = new SingleActTime();
		cfgInfo.setCfgId(specialTimecfg.getCfgId());
		cfgInfo.setActDesc(specialTimecfg.getActDesc());
		cfgInfo.setStartTime(specialTimecfg.getStartTime());
		cfgInfo.setEndTime(specialTimecfg.getEndTime());
		cfgInfo.setStartViceTime(specialTimecfg.getStartViceTime());
		cfgInfo.setEndViceTime(specialTimecfg.getEndViceTime());
		cfgInfo.setRangeTime(specialTimecfg.getRangeTime());
		return cfgInfo;
	}
}
