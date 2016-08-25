package com.playerdata.activity.limitHeroType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ActivityLimitHeroBoxCfgDAO extends CfgCsvDao<ActivityLimitHeroBoxCfg>{
	public static ActivityLimitHeroBoxCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityLimitHeroBoxCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityLimitHeroBoxCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityLimitHeroBoxCfg.csv", ActivityLimitHeroBoxCfg.class);			
		return cfgCacheMap;
	}


	
	
//	/**根据传入的id来查找激活的子活动*/
//	public ActivityVitalityRewardCfg getById(String subId){
//		ActivityVitalityRewardCfg target = new ActivityVitalityRewardCfg();
//		List<ActivityVitalityRewardCfg> allCfg = getAllCfg();
//		for (ActivityVitalityRewardCfg cfg : allCfg) {
//			if(StringUtils.equals(cfg.getId(), subId)){
//				target = cfg;
//				break;
//			}
//		}
//		return target;		
//	}
//	
	
	
	
	
}
