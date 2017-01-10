package com.playerdata.activity.VitalityType.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/**
 * 活跃之王子表
 * @author aken
 *
 */
public final class ActivityVitalitySubCfgDAO extends CfgCsvDao<ActivityVitalitySubCfg> {

	public static ActivityVitalitySubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityVitalitySubCfgDAO.class);
	}

	//private HashMap<String, List<ActivityVitalitySubCfg>> subCfgListMap ;
	
	@Override
	public Map<String, ActivityVitalitySubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityVitalityTypeSubCfg.csv", ActivityVitalitySubCfg.class);	
//		HashMap<String, List<ActivityVitalitySubCfg>> subCfgListMapTmp = new HashMap<String, List<ActivityVitalitySubCfg>>();
//		for(ActivityVitalitySubCfg subCfg: cfgCacheMap.values()){
//			ActivityTypeHelper.add(subCfg, subCfg.getType(), subCfgListMapTmp);
//		}
		//this.subCfgListMap = subCfgListMapTmp;
		return cfgCacheMap;
	}
}