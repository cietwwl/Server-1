package com.playerdata.activity.countType.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityCountTypeSubCfgDAO extends CfgCsvDao<ActivityCountTypeSubCfg> {


	public static ActivityCountTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityCountTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityCountTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityCountTypeSubCfg.csv", ActivityCountTypeSubCfg.class);
		return cfgCacheMap;
	}
	


	public List<ActivityCountTypeSubCfg> getByParentCfgId(String parentCfgId){
		List<ActivityCountTypeSubCfg> targetList = new ArrayList<ActivityCountTypeSubCfg>();
		List<ActivityCountTypeSubCfg> allCfg = getAllCfg();
		for (ActivityCountTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}
	public ActivityCountTypeSubCfg getById(String subId){
		ActivityCountTypeSubCfg target = new ActivityCountTypeSubCfg();
		List<ActivityCountTypeSubCfg> allCfg = getAllCfg();
		for (ActivityCountTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getId(), subId)){
				target = tmpItem;
			}
		}
		return target;
		
	}
	
	


}