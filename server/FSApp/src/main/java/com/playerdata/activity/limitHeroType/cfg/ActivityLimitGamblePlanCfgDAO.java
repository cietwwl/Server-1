package com.playerdata.activity.limitHeroType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class ActivityLimitGamblePlanCfgDAO extends CfgCsvDao<ActivityLimitGamblePlanCfg> {


	
	public static ActivityLimitGamblePlanCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityLimitGamblePlanCfgDAO.class);
	}	
	
	
	@Override
	public Map<String, ActivityLimitGamblePlanCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityLimitGamblePlanCfg.csv", ActivityLimitGamblePlanCfg.class);
		parse();
		return cfgCacheMap;
	}

	public ActivityLimitGamblePlanCfg getCfgByType(int lotteryDrawType, int level){
		for (Iterator<Entry<String, ActivityLimitGamblePlanCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, ActivityLimitGamblePlanCfg> next = iterator.next();
			ActivityLimitGamblePlanCfg cfg = next.getValue();
			if(cfg.getDropType() == lotteryDrawType && cfg.getLevelMin() <= level && cfg.getLevelMax() >= level){
				return cfg;
			}	
		}
		return null;
	}


	private void parse() {
		for (Iterator<Entry<String, ActivityLimitGamblePlanCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, ActivityLimitGamblePlanCfg> entry = iterator.next();
			ActivityLimitGamblePlanCfg cfg = entry.getValue();
			String lvPeriod = cfg.getLevelSegment();
			String[] split = lvPeriod.split("~");
			int minLv = Integer.parseInt(split[0]);
			int maxLv = Integer.parseInt(split[1]);
			cfg.setLevelMin(minLv);
			cfg.setLevelMax(maxLv);
			
			String checknum = cfg.getGuaranteeCheckNum();
			String[] guaranteeSplit = checknum.split("\\|");
			List<Integer> tmpList = new ArrayList<Integer>();
			int length = guaranteeSplit.length;
			for(String str: guaranteeSplit){
				tmpList.add(Integer.parseInt(str));
				length--;
				if(length == 1){
					break;
				}
			}
			cfg.setGuaranteeList(tmpList);
			cfg.setMaxGuarantee(Integer.parseInt(guaranteeSplit[guaranteeSplit.length - 1]));
	
			Map<Integer,Integer> freeRewardPlanMap = parseScript(cfg.getOrdinaryFreePlan());
			cfg.setOrdinaryFreePlanMap(freeRewardPlanMap);
			
			Map<Integer,Integer> payRewardPlanMap = parseScript(cfg.getOrdinaryPlan());			
			cfg.setOrdinaryPlanMap(payRewardPlanMap);
			
			Map<Integer,Integer> guaranteeRewardPlanMap = parseScript(cfg.getGuaranteePlan());			
			cfg.setGuaranteePlanMap(guaranteeRewardPlanMap);
		}
		
	}
	

	private Map<Integer,Integer> parseScript(String value){
		Map<Integer, Integer> ordinaryFreePlanMap = new HashMap<Integer, Integer>();
		String[] split = value.split(",");
		for (String str : split) {
			String[] subSplit = str.split("_");
			ordinaryFreePlanMap.put(Integer.parseInt(subSplit[0]), Integer.parseInt(subSplit[1]));
		}
		return ordinaryFreePlanMap;
	}


}