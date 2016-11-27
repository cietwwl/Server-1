package com.bm.worldBoss.cfg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class WBCfgDAO extends CfgCsvDao<WBCfg> {	


	public static WBCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBCfgDAO.class);
	}

	@Override
	public Map<String, WBCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBCfg.csv", WBCfg.class);
		return cfgCacheMap;
	}
	
	

	public WBCfg getNextCfg(){
		Calendar currentDay = DateUtils.getCurrent();
		int weekDay = currentDay.get(Calendar.DAY_OF_WEEK);// 当前天数
//		int curHour = currentDay.get(Calendar.HOUR_OF_DAY);// 当前小时
		List<WBCfg> todayCfgs = getTodayCfgs(weekDay);
		
		WBCfg target = null;
		long targetPreStartTime = 0;
		long curTime = System.currentTimeMillis();

		for (WBCfg wbCfgTmp : todayCfgs) {
			long tmpPreStartTime = wbCfgTmp.getPreStartTime();
			if(tmpPreStartTime > curTime){
				if(targetPreStartTime == 0 || tmpPreStartTime < targetPreStartTime){
					targetPreStartTime = tmpPreStartTime;
					target = wbCfgTmp;
				}				
			}
			
		}
		
		return target;
	}
	
	
	public List<WBCfg> getTodayCfgs(int weekDay){
		
		List<WBCfg> todayCfgs = new ArrayList<WBCfg>();
		List<WBCfg> allCfg = getAllCfg();
		
		//配置的weekday是从0开始，（星期天就是1-1=0），策划要求
		int cfgWeekDay = weekDay-1;
		for (WBCfg wbCfg : allCfg) {
			if(cfgWeekDay == wbCfg.getWeekDay()){
				todayCfgs.add(wbCfg);
			}
		}
		return todayCfgs;	
		
	}


	
	

}