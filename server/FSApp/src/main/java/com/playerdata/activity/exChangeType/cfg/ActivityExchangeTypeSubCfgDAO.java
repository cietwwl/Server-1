package com.playerdata.activity.exChangeType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.ActivityTypeHelper;
import com.playerdata.activity.countType.cfg.ActivityCountTypeSubCfg;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.fsutil.util.StringUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class ActivityExchangeTypeSubCfgDAO extends CfgCsvDao<ActivityExchangeTypeSubCfg> {


	public static ActivityExchangeTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityExchangeTypeSubCfgDAO.class);
	}

	private HashMap<String, List<ActivityExchangeTypeSubCfg>> subCfgListMap;
	
	
	@Override
	public Map<String, ActivityExchangeTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityExchangeTypeSubCfg.csv", ActivityExchangeTypeSubCfg.class);
		for (ActivityExchangeTypeSubCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}		
		HashMap<String, List<ActivityExchangeTypeSubCfg>> subCfgListMapTmp = new HashMap<String, List<ActivityExchangeTypeSubCfg>>();
		for(ActivityExchangeTypeSubCfg subCfg : cfgCacheMap.values()){
			ActivityTypeHelper.add(subCfg, subCfg.getParentCfg(), subCfgListMapTmp);
		}
		this.subCfgListMap = subCfgListMapTmp;		
		return cfgCacheMap;
	}

	/**解析a_a1,b_b1*/
	private void parseTime(ActivityExchangeTypeSubCfg cfgTmp) {
		Map<String, String> changelisttmp = new HashMap<String, String>();
		if(StringUtils.isBlank(cfgTmp.getExchangeneed())){
			GameLog.error(LogModule.ComActivityExchange, "", "配置文件没有exchangeneed字段", null);
			return;
		}
		String[] changeStrs = cfgTmp.getExchangeneed().split(",");
		for(String tmp :changeStrs){
			String[] Strs = tmp.split("_");
			changelisttmp.put(Strs[0],Strs[1] );
		}
		cfgTmp.setChangelist(changelisttmp);
	}
	
	public List<ActivityExchangeTypeSubCfg> getByParentCfgId(String parentCfgId){
		return subCfgListMap.get(parentCfgId);			
	}


	public ActivityExchangeTypeSubCfg getById(String cfgId) {
		ActivityExchangeTypeSubCfg cfg = null;
		List<ActivityExchangeTypeSubCfg> cfglist = getAllCfg();
		for(ActivityExchangeTypeSubCfg subcfg : cfglist){
			if(StringUtils.equals(subcfg.getId(), cfgId)){
				cfg = subcfg;
				break;
			}
		}
		return cfg;
	}
	

}