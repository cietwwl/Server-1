package com.playerdata.activity.dateType.cfg;

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
public final class ActivityDateTypeSubCfgDAO extends CfgCsvDao<ActivityDateTypeSubCfg> {


	public static ActivityDateTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityDateTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityDateTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityTimeCardTypeSubCfg.csv", ActivityDateTypeSubCfg.class);
		return cfgCacheMap;
	}
	


	public List<ActivityDateTypeSubCfg> getByParentCfgId(String parentCfgId){
		List<ActivityDateTypeSubCfg> targetList = new ArrayList<ActivityDateTypeSubCfg>();
		List<ActivityDateTypeSubCfg> allCfg = getAllCfg();
		for (ActivityDateTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}
	public ActivityDateTypeSubCfg getById(String subId){
		ActivityDateTypeSubCfg target = new ActivityDateTypeSubCfg();
		List<ActivityDateTypeSubCfg> allCfg = getAllCfg();
		for (ActivityDateTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getId(), subId)){
				target = tmpItem;
			}
		}
		return target;
		
	}
	
	


}