package com.playerdata.activity.exChangeType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class ActivityExchangeTypeDropCfgDAO extends CfgCsvDao<ActivityExchangeTypeDropCfg>{
	public static ActivityExchangeTypeDropCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityExchangeTypeDropCfgDAO.class);
	}

	
	@Override
	public Map<String, ActivityExchangeTypeDropCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityExchangeTypeDropCfg.csv", ActivityExchangeTypeDropCfg.class);			
		for (ActivityExchangeTypeDropCfg cfgTmp : cfgCacheMap.values()) {
			parseTime(cfgTmp);
		}	
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


	/**根据传入的id来查找激活的子活动*/
	public ActivityExchangeTypeDropCfg getById(String subId){
		ActivityExchangeTypeDropCfg target = new ActivityExchangeTypeDropCfg();
		List<ActivityExchangeTypeDropCfg> allCfg = getAllCfg();
		for (ActivityExchangeTypeDropCfg cfg : allCfg) {
			if(StringUtils.equals(cfg.getId(), subId)){
				target = cfg;
				break;
			}
		}
		return target;		
	}
	
	/**根据传入的父类id来查找激活的子活动列表*/
	public List<ActivityExchangeTypeDropCfg> getByParentId(String subId){
		List<ActivityExchangeTypeDropCfg> cfgList = new ArrayList<ActivityExchangeTypeDropCfg>();
		List<ActivityExchangeTypeDropCfg> allCfg = getAllCfg();
		for (ActivityExchangeTypeDropCfg cfg : allCfg) {
			if(StringUtils.equals(cfg.getParentCfg(), subId)){
				cfgList.add(cfg);		
			}
		}
		return cfgList;
	}
	
	
	
}