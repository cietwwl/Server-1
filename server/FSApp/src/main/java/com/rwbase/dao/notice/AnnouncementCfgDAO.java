package com.rwbase.dao.notice;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.DateUtils;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.manager.GameManager;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.notice.pojo.AnnouncementCfg;


public class AnnouncementCfgDAO extends CfgCsvDao<AnnouncementCfg> {
	
	private static int PushTimeType_One = 1;  	 //相对开服时间
	private static int PushTimeType_Two = 2;  	 //绝对时间
	
	public static AnnouncementCfgDAO getInstance() {
		return SpringContextUtil.getBean(AnnouncementCfgDAO.class);
	}
	
	@Override
	protected Map<String, AnnouncementCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Announcement/Announcement.csv",AnnouncementCfg.class);
		parse();
		return cfgCacheMap;
	}

	public void parse(){
		long openTime = GameManager.getOpenTime();
		long fiveTime = DateUtils.getHour(openTime, 5);
		for (Iterator<Entry<String, AnnouncementCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, AnnouncementCfg> entry = iterator.next();
			
			AnnouncementCfg cfg = entry.getValue();
			String pushTime = cfg.getPushTime();
			String[] split = pushTime.split(":");
			int pushTimeType = Integer.parseInt(split[0]);
			if(pushTimeType == PushTimeType_One){
				int startDay = Integer.parseInt(split[1]);
				int durationDay = Integer.parseInt(split[2]);
				cfg.setStartTime(fiveTime + startDay * DateUtils.DayTime);
				cfg.setEndTime(fiveTime + (startDay + durationDay) * DateUtils.DayTime); 
				
			}else if(pushTimeType == PushTimeType_Two){
				String strStartTime =split[1];
				String strEndTime = split[2];
				long startTime = DateUtils.getDateTimeFormatTime(strStartTime, "yyyy-MM-dd", 5);
				long endTime = DateUtils.getDateTimeFormatTime(strEndTime, "yyyy-MM-dd", 5);
				cfg.setStartTime(startTime);
				cfg.setEndTime(endTime);
			}
			
		}
	}
}
