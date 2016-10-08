package com.rwbase.dao.randomBoss.cfg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RandomBossCfgDao extends CfgCsvDao<RandomBossCfg>{
	
	
	/**Map<key,Pair<总权重，Map<Pair<权重下限，权重上限>，配置>>*/
	private Map<RandomBossLevelKey, Pair<Integer, Map<Pair<Integer, Integer>, RandomBossCfg>>> levelMap = new HashMap<RandomBossLevelKey, Pair<Integer,Map<Pair<Integer,Integer>,RandomBossCfg>>>();
	
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
			Pair<Integer, Map<Pair<Integer, Integer>, RandomBossCfg>> pair = levelMap.get(key);
			if(pair == null){
				Map<Pair<Integer, Integer>, RandomBossCfg> map = new HashMap<Pair<Integer,Integer>, RandomBossCfg>();
				Pair<Integer, Integer> innerPair = Pair.Create(0, cfg.getWeight());
				map.put(innerPair, cfg);
				pair = Pair.Create(cfg.getWeight(), map);
				levelMap.put(key, pair);
			}else{
				Integer t1 = pair.getT1();
				Map<Pair<Integer, Integer>, RandomBossCfg> map = pair.getT2();
				Pair<Integer, Integer> innerPair = Pair.Create(t1 + 1, t1 + cfg.getWeight());
				map.put(innerPair, cfg);
				pair.setT1(t1 + cfg.getWeight());
			}
			
		}
		
	}
	
	
	/**
	 * 获取等级对应的boss列表
	 * @param level
	 * @return
	 */
	public Pair<Integer,Map<Pair<Integer,Integer>,RandomBossCfg>> getLvCfgs(int level){
		Set<RandomBossLevelKey> keySet = levelMap.keySet();
		RandomBossLevelKey cfgKey = null;
		for (RandomBossLevelKey key : keySet) {
			if(key.match(level)){
				cfgKey = key;
				break;
			}
		}
		
		if(cfgKey == null){
			return null;
		}
		return levelMap.get(cfgKey);
	}

	
	
}
