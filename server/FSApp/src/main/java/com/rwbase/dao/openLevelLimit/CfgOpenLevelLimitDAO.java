package com.rwbase.dao.openLevelLimit;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;

public class CfgOpenLevelLimitDAO extends CfgCsvDao<CfgOpenLevelLimit> {

	private static CfgOpenLevelLimitDAO instance = new CfgOpenLevelLimitDAO();

	private CfgOpenLevelLimitDAO() {

	}

	public static CfgOpenLevelLimitDAO getInstance() {
		return instance;
	}

	public Map<String, CfgOpenLevelLimit> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("openLevelLimit/openLevelLimit.csv", CfgOpenLevelLimit.class);
		return cfgCacheMap;
	}

	/** 等级开放 */
	public boolean isOpen(eOpenLevelType type, int level) {
		boolean result = false;
		CfgOpenLevelLimit cfg = (CfgOpenLevelLimit) getCfgById(type.getOrderString());
		if (cfg != null) {
			if (level >= cfg.getMinLevel() && level <= cfg.getMaxLevel()) {
				result = true;
			} else if (cfg.getMaxLevel() <= 0 || level > cfg.getMaxLevel()) {
				return false;
			} else {
				result = false;
			}
		}
		return result;
	}

	/**
	 * 判断是否开放等级
	 * 
	 * @param type 检测开放的功能类型
	 * @param level 当前角色的等级
	 * @return 返回开启需要的等级
	 */
	public int checkIsOpen(eOpenLevelType type, int level) {
		CfgOpenLevelLimit cfg = (CfgOpenLevelLimit) getCfgById(type.getOrderString());
		if (cfg == null) {
			return -1;
		}

		int minLevel = cfg.getMinLevel();
		if (level >= minLevel) {
			return -1;
		}

		return minLevel;
	}
}