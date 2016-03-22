package com.rwbase.dao.worship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.worship.pojo.CfgWorshipRandomReward;

public class CfgWorshipRandomRewardHelper extends CfgCsvDao<CfgWorshipRandomReward>{
	private static CfgWorshipRandomRewardHelper instance = new CfgWorshipRandomRewardHelper();
	private CfgWorshipRandomRewardHelper() {
		
	}
	
	private Map<String, List<CfgWorshipRandomReward>> weightMap = new HashMap<String, List<CfgWorshipRandomReward>>();
	private void initWeight(Map<String, CfgWorshipRandomReward> cfgCacheMapTmp){
		// author：lida 方便热加载改动一下这里的初始化
		// initJsonCfg();
		
		Map<String, List<CfgWorshipRandomReward>> weightMapTmp = new HashMap<String, List<CfgWorshipRandomReward>>();
		getAllCfg();
				
		Iterator<CfgWorshipRandomReward> it = cfgCacheMapTmp.values().iterator();
		while(it.hasNext()){
			CfgWorshipRandomReward cfg = it.next();
			String key = cfg.getScheme() + "_" + cfg.getWeightGroup();
			if(!weightMapTmp.containsKey(key)){
				weightMapTmp.put(key, new ArrayList<CfgWorshipRandomReward>());
			}
			weightMapTmp.get(key).add(cfg);
		}
	}
	
	public static CfgWorshipRandomRewardHelper getInstance(){
		return instance;
	}
	
	public Map<String, CfgWorshipRandomReward> initJsonCfg() {
		Map<String, CfgWorshipRandomReward> cfgCacheMapTmp = CfgCsvHelper.readCsv2Map("worship/worshipRandomReward.csv",CfgWorshipRandomReward.class);
		initWeight(cfgCacheMapTmp);
		cfgCacheMap = cfgCacheMapTmp;
		return cfgCacheMap;
	}
	
	/**
	 * 根据膜拜掉落方案与权重组获取
	 * @param scheme
	 * @param weightGroup
	 * @return
	 */
	public List<CfgWorshipRandomReward> getWorshipRewardCfg(int scheme, int weightGroup){		
		String key = scheme + "_" + weightGroup;
		if(weightMap.containsKey(key)){
			return weightMap.get(key);
		}
		return null;
	}
}
