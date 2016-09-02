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

public class ActivityLimitGambleDropCfgDAO extends CfgCsvDao<ActivityLimitGambleDropCfg> {
	private HashMap<Integer, List<ActivityLimitGambleDropCfg>> ActivityLimitGambleDropMap = new HashMap<Integer, List<ActivityLimitGambleDropCfg>>();
	/**
	 * 奖励池的权重和
	 */
	private HashMap<Integer, Integer> SumWeightMap = new HashMap<Integer, Integer>();
	
	public static ActivityLimitGambleDropCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityLimitGambleDropCfgDAO.class);
	}

	@Override
	protected Map<String, ActivityLimitGambleDropCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityLimitGambleDropCfg.csv",ActivityLimitGambleDropCfg.class);
		parse();
		return cfgCacheMap;
	}
	
	private void parse(){		
		for (Iterator<Entry<String, ActivityLimitGambleDropCfg>> iterator = cfgCacheMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, ActivityLimitGambleDropCfg> next = iterator.next();
			ActivityLimitGambleDropCfg cfg = next.getValue();
			int groupId = cfg.getItemGroup();
			if (ActivityLimitGambleDropMap.containsKey(groupId)) {
				List<ActivityLimitGambleDropCfg> list = ActivityLimitGambleDropMap.get(groupId);
				list.add(cfg);
			} else {
				List<ActivityLimitGambleDropCfg> list = new ArrayList<ActivityLimitGambleDropCfg>();
				list.add(cfg);
				ActivityLimitGambleDropMap.put(groupId, list);
			}
			Integer value = SumWeightMap.get(groupId);
			int sum = cfg.getWeight();
			if (value != null) {
				sum += value;
			}
			SumWeightMap.put(groupId, sum);
		}
	}
	
	
	
	public List<ActivityLimitGambleDropCfg> getActivityLimitGambleDropCfgByPoolId(int poolId){
		List<ActivityLimitGambleDropCfg> list = ActivityLimitGambleDropMap.get(poolId);
		return list;
	}
	
	public int getSumWeightByPoolId(int poolId){
		return SumWeightMap.get(poolId);
	}
}
