package com.rwbase.dao.battletower.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerBoxCfg;

/*
 * @author HC
 * @date 2015年9月16日 上午10:58:00
 * @Description 
 */
public class BattleTowerBoxCfgDao extends CfgCsvDao<BattleTowerBoxCfg> {
	private static BattleTowerBoxCfgDao cfgDao;

	public static BattleTowerBoxCfgDao getCfgDao() {
		if (cfgDao == null) {
			cfgDao = new BattleTowerBoxCfgDao();
		}
		return cfgDao;
	}

	@Override
	public Map<String, BattleTowerBoxCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battleTower/BattleTowerBoxCfg.csv", BattleTowerBoxCfg.class);
		return cfgCacheMap;
	}
}