package com.rwbase.dao.battletower.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerBossBreakEvenCfg;

/*
 * @author HC
 * @date 2015年9月8日 下午5:07:45
 * @Description 
 */
public class BattleTowerBossBreakEvenCfgDao extends CfgCsvDao<BattleTowerBossBreakEvenCfg> {
	public static BattleTowerBossBreakEvenCfgDao getCfgDao() {
		return SpringContextUtil.getBean(BattleTowerBossBreakEvenCfgDao.class);
	}

	@Override
	public Map<String, BattleTowerBossBreakEvenCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battleTower/BattleTowerBossBreakEven.csv", BattleTowerBossBreakEvenCfg.class);
		return cfgCacheMap;
	}
}