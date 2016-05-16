package com.playerdata.activity.timeCountType.cfg;

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
public final class ActivityTimeCountTypeSubCfgDAO extends CfgCsvDao<ActivityTimeCountTypeSubCfg> {


	public static ActivityTimeCountTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityTimeCountTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityTimeCountTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityTimeCountTypeSubCfg.csv", ActivityTimeCountTypeSubCfg.class);	
		return cfgCacheMap;
	}
	


	public List<ActivityTimeCountTypeSubCfg> getByParentCfgId(String parentCfgId){
		List<ActivityTimeCountTypeSubCfg> targetList = new ArrayList<ActivityTimeCountTypeSubCfg>();
		List<ActivityTimeCountTypeSubCfg> allCfg = getAllCfg();
		for (ActivityTimeCountTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}
	public ActivityTimeCountTypeSubCfg getById(String subId){
		ActivityTimeCountTypeSubCfg target = new ActivityTimeCountTypeSubCfg();
		List<ActivityTimeCountTypeSubCfg> allCfg = getAllCfg();
		for (ActivityTimeCountTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getId(), subId)){
				target = tmpItem;
			}
		}
		return target;
		
	}
	
	


}