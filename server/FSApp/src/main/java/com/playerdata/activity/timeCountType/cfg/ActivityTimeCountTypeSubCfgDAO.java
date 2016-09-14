package com.playerdata.activity.timeCountType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activity.ActivityTypeHelper;
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
	
	private HashMap<String, List<ActivityTimeCountTypeSubCfg>> subCfgMapListByParentid;
	
	
	@Override
	public Map<String, ActivityTimeCountTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityTimeCountTypeSubCfg.csv", ActivityTimeCountTypeSubCfg.class);	
		HashMap<String, List<ActivityTimeCountTypeSubCfg>> subCfgMap= new HashMap<String, List<ActivityTimeCountTypeSubCfg>>();
		for(ActivityTimeCountTypeSubCfg subCfg : cfgCacheMap.values()){
			ActivityTypeHelper.add(subCfg, subCfg.getParentId(), subCfgMap);
		}
		this.subCfgMapListByParentid = subCfgMap;
		return cfgCacheMap;
	}
	


	public List<ActivityTimeCountTypeSubCfg> getByParentCfgId(String parentCfgId){
		return subCfgMapListByParentid.get(parentCfgId);		
	}
	public ActivityTimeCountTypeSubCfg getById(String subId){
		return cfgCacheMap.get(subId);
	}
	
	


}