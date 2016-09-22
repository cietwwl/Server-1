package com.rwbase.dao.randomBoss.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class RBServerCfgDao extends CfgCsvDao<RandomBossServerCfg>{

	public static RBServerCfgDao getInstance(){
		return SpringContextUtil.getBean(RBServerCfgDao.class);
	}
	
	
	@Override
	protected Map<String, RandomBossServerCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("randomBoss/RandomBossServerCfg.csv", RandomBossServerCfg.class);
		return cfgCacheMap;
	}
	
	/**
	 * 默认获取第一个配置
	 * @return
	 */
	public RandomBossServerCfg getDefaultCfg(){
		RandomBossServerCfg cfg = null;
		for (RandomBossServerCfg data : cfgCacheMap.values()) {
			cfg = data;
			break;
		}
		return cfg;
	}

}
