package com.rwbase.dao.battletower.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battletower.pojo.cfg.BattleTowerBoxCfg;

/*
 * @author HC
 * @date 2015年9月16日 上午10:58:00
 * @Description 
 */
public class BattleTowerBoxCfgDao extends CfgCsvDao<BattleTowerBoxCfg> {

	public static BattleTowerBoxCfgDao getCfgDao() {
		return SpringContextUtil.getBean(BattleTowerBoxCfgDao.class);
	}

	@Override
	public Map<String, BattleTowerBoxCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battleTower/BattleTowerBoxCfg.csv", BattleTowerBoxCfg.class);
		return cfgCacheMap;
	}
}