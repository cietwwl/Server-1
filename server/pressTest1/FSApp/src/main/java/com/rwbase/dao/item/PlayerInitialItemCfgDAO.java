package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.PlayerInitialItemCfg;

public final class PlayerInitialItemCfgDAO extends CfgCsvDao<PlayerInitialItemCfg> {
	private static final String UNIQUE_KEY = "1";// 唯一的Key
	private PlayerInitialItemCfg uniqueCfg;// 唯一的配置

	private static PlayerInitialItemCfgDAO instance = new PlayerInitialItemCfgDAO();

	private PlayerInitialItemCfgDAO() {
		Map<String, PlayerInitialItemCfg> initJsonCfg = initJsonCfg();// 调用实例
		uniqueCfg = initJsonCfg.get(UNIQUE_KEY);// 唯一的配置
	}

	@Override
	public Map<String, PlayerInitialItemCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/PlayerInitialItem.csv", PlayerInitialItemCfg.class);
		return cfgCacheMap;
	}

	/**
	 * 获取唯一的配置
	 * 
	 * @return
	 */
	public static PlayerInitialItemCfg getUniqueCfg() {
		return instance.uniqueCfg;
	}
}