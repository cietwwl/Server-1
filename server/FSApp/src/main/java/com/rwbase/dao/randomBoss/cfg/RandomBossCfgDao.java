package com.rwbase.dao.randomBoss.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RandomBossCfgDao extends CfgCsvDao<RandomBossCfg>{
	
	private Map<RandomBossLevelKey, List<RandomBossCfg>> levelMap = new HashMap<RandomBossLevelKey, List<RandomBossCfg>>(); 
	
	public static RandomBossCfgDao getInstance(){
		return SpringContextUtil.getBean(RandomBossCfgDao.class);
	}
	
	

	@Override
	protected Map<String, RandomBossCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("randomBoss/RandomBossCfg.csv", RandomBossCfg.class);
		return cfgCacheMap;
	}



	@Override
	public void CheckConfig() {
		Collection<RandomBossCfg> values = cfgCacheMap.values();
		for (RandomBossCfg cfg : values) {
			cfg.format();
			
			
			RandomBossLevelKey key = new RandomBossLevelKey(cfg.getUpperLv(), cfg.getLowerLv());
			List<RandomBossCfg> list = levelMap.get(key);
			if(list == null){
				list = new ArrayList<RandomBossCfg>();
				levelMap.put(key, list);
			}
			list.add(cfg);
		}
		
	}
	
	
	/**
	 * 获取等级对应的boss列表
	 * @param level
	 * @return
	 */
	public List<RandomBossCfg> getLvCfgs(int level){
		Set<RandomBossLevelKey> keySet = levelMap.keySet();
		RandomBossLevelKey cfgKey = null;
		for (RandomBossLevelKey key : keySet) {
			if(key.match(level)){
				cfgKey = key;
				break;
			}
		}
		
		if(cfgKey == null){
			return Collections.emptyList();
		}
		return levelMap.get(cfgKey);
	}

	
	
}
