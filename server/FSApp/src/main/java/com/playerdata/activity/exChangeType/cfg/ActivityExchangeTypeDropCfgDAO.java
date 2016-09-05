package com.playerdata.activity.exChangeType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ActivityExchangeTypeDropCfgDAO extends CfgCsvDao<ActivityExchangeTypeDropCfg>{
	public static ActivityExchangeTypeDropCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityExchangeTypeDropCfgDAO.class);
	}

	private HashMap<String, List<ActivityExchangeTypeDropCfg>> dropCfgListMap;
	
	@Override
	public Map<String, ActivityExchangeTypeDropCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityExchangeTypeDropCfg.csv", ActivityExchangeTypeDropCfg.class);			
		for (ActivityExchangeTypeDropCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}	
		HashMap<String, List<ActivityExchangeTypeDropCfg>> dropCfgListMapTmp = new HashMap<String, List<ActivityExchangeTypeDropCfg>>();
		for(ActivityExchangeTypeDropCfg dropCfg : cfgCacheMap.values()){
			ActivityTypeHelper.add(dropCfg, dropCfg.getParentCfg(), dropCfgListMapTmp);
		}
		this.dropCfgListMap = dropCfgListMapTmp;
		
		return cfgCacheMap;		
	}
	
	
	
	
	
	/**解析copytype_几率,copyType_几率*/
	private void parseTime(ActivityExchangeTypeDropCfg cfgTmp) {
		Map<Integer, Integer[]> droplisttmp = new HashMap<Integer, Integer[]>();
		if(StringUtils.isBlank(cfgTmp.getDrop())){
			GameLog.error(LogModule.ComActivityExchange, "", "配置文件没有exchangeneed字段", null);
			return;
		}
		String[] dropStrs = cfgTmp.getDrop().split(",");
		for(String tmp :dropStrs){
			String[] Strs = tmp.split("_");			
			if(Strs.length!=3){
				GameLog.error("exchangetypedropcfgdao", null, "掉率解析异常", null);
				break;
			}
			Integer[] numAndProbility = new Integer[2];
			numAndProbility[0] =Integer.parseInt(Strs[1]);
			numAndProbility[1] =Integer.parseInt(Strs[2]);
			droplisttmp.put(Integer.parseInt(Strs[0]),numAndProbility );
		}
		cfgTmp.setDropMap(droplisttmp);
	}

	
	/**根据传入的父类id来查找激活的子活动列表*/
	public List<ActivityExchangeTypeDropCfg> getByParentId(String subId){
		return dropCfgListMap.get(subId);
	}
	
	
	
}
