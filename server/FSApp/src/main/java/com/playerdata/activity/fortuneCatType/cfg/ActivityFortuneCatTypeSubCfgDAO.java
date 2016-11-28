package com.playerdata.activity.fortuneCatType.cfg;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class ActivityFortuneCatTypeSubCfgDAO extends CfgCsvDao<ActivityFortuneCatTypeSubCfg> {
	public static ActivityFortuneCatTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityFortuneCatTypeSubCfgDAO.class);
	}
	
	private HashMap<String, List<ActivityFortuneCatTypeSubCfg>> subCfgListMap ;
	
	@Override
	public Map<String, ActivityFortuneCatTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityFortunCatTypeSubCfg.csv", ActivityFortuneCatTypeSubCfg.class);
		HashMap<String, List<ActivityFortuneCatTypeSubCfg>> subCfgListMapTmp = new HashMap<String, List<ActivityFortuneCatTypeSubCfg>>();
		for(ActivityFortuneCatTypeSubCfg subCfg: cfgCacheMap.values()){
			ActivityTypeHelper.add(subCfg, String.valueOf(subCfg.getParentid()), subCfgListMapTmp);
		}
		for(Entry<String, List<ActivityFortuneCatTypeSubCfg>> entry : subCfgListMapTmp.entrySet()){
			Collections.sort(entry.getValue(), new Comparator<ActivityFortuneCatTypeSubCfg>(){
				@Override
				public int compare(ActivityFortuneCatTypeSubCfg o1,
						ActivityFortuneCatTypeSubCfg o2) {
					if(o1.getNum() > o2.getNum()) return 1;
					if(o1.getNum() < o2.getNum()) return -1;
					return 0;
				}
			});
		}
		this.subCfgListMap = subCfgListMapTmp;
		return cfgCacheMap;
	}
	
	public List<ActivityFortuneCatTypeSubCfg> getCfgListByParentId(String cfgId) {
		return subCfgListMap.get(cfgId);
	}
}