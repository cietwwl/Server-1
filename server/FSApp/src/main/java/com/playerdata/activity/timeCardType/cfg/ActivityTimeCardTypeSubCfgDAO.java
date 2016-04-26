package com.playerdata.activity.timeCardType.cfg;

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
public final class ActivityTimeCardTypeSubCfgDAO extends CfgCsvDao<ActivityTimeCardTypeSubCfg> {


	public static ActivityTimeCardTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityTimeCardTypeSubCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityTimeCardTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityTmeCardTypeSubCfg.csv", ActivityTimeCardTypeSubCfg.class);
		return cfgCacheMap;
	}
	


	public List<ActivityTimeCardTypeSubCfg> getByParentCfgId(String parentCfgId){
		List<ActivityTimeCardTypeSubCfg> targetList = new ArrayList<ActivityTimeCardTypeSubCfg>();
		List<ActivityTimeCardTypeSubCfg> allCfg = getAllCfg();
		for (ActivityTimeCardTypeSubCfg tmpItem : allCfg) {
			if(StringUtils.equals(tmpItem.getParentCfgId(), parentCfgId)){
				targetList.add(tmpItem);
			}
		}
		return targetList;
		
	}
	
	


}