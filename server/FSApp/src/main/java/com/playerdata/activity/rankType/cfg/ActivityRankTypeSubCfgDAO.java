package com.playerdata.activity.rankType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityRankTypeSubCfgDAO extends CfgCsvDao<ActivityRankTypeSubCfg> {


	public static ActivityRankTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityRankTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityRankTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityRankTypeSubCfg.csv", ActivityRankTypeSubCfg.class);
		return cfgCacheMap;
	}
	


	public List<ActivityRankTypeSubCfg> getByParentCfgId(String parentCfgId){
		List<ActivityRankTypeSubCfg> targetList = new ArrayList<ActivityRankTypeSubCfg>();
		List<ActivityRankTypeSubCfg> allCfg = getAllCfg();
		for (ActivityRankTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}
	


}