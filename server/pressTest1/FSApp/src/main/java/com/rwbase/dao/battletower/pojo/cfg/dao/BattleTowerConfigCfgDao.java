package com.rwbase.dao.battletower.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerConfigCfg;

/*
 * @author HC
 * @date 2015年9月2日 下午4:59:17
 * @Description 试练塔配置
 * @Notice：注意的一点是这个配置的Key是battleTowerConfigKey
 */
public class BattleTowerConfigCfgDao extends CfgCsvDao<BattleTowerConfigCfg> {
	/** 整份配置的唯一标识Key */
	private static final String UNIQUE_KEY = "battleTowerConfigKey";
	private static BattleTowerConfigCfg unique_cfg;// 唯一的配置
	// private static int strategy_cache_record_size;// 战略缓存记录长度

	private static BattleTowerConfigCfgDao cfgDao;

	public static BattleTowerConfigCfgDao getCfgDao() {
		if (cfgDao == null) {
			cfgDao = new BattleTowerConfigCfgDao();
		}
		return cfgDao;
	}

	@Override
	public Map<String, BattleTowerConfigCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battleTower/BattleTowerConfigCfg.csv", BattleTowerConfigCfg.class);
		return cfgCacheMap;
	}

	/**
	 * 获取唯一的一个试练塔配置
	 * 
	 * @return
	 */
	public BattleTowerConfigCfg getUniqueCfg() {
		if (unique_cfg == null) {
			unique_cfg = (BattleTowerConfigCfg) getCfgById(UNIQUE_KEY);
		}

		return unique_cfg;
	}

	// /**
	// * 获取战略缓存的长度
	// *
	// * @return
	// */
	// public int getStrategyCacheRecordSize() {
	// if (unique_cfg == null) {
	// unique_cfg = (BattleTowerConfigCfg) getCfgById(UNIQUE_KEY);
	// }
	//
	// return unique_cfg.getStrategyCacheRecordSize();
	// }
}