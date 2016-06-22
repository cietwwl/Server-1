package com.rwbase.dao.arena;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.arena.pojo.ArenaInfoCfg;
import com.rwbase.dao.copypve.CopyType;

public class ArenaInfoCfgDAO extends CfgCsvDao<ArenaInfoCfg> {

	private ArenaInfoCfgDAO() {
	}

	public static ArenaInfoCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaInfoCfgDAO.class);
	}

	@Override
	public Map<String, ArenaInfoCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaInfo.csv", ArenaInfoCfg.class);
		Iterable<ArenaInfoCfg> values = getIterateAllCfg();
		for (ArenaInfoCfg cfg : values) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}

	public ArenaInfoCfg getArenaInfo() {
		List<ArenaInfoCfg> list = getAllCfg();
		for (ArenaInfoCfg cfg : list) {
			if (cfg.getCopyType() == CopyType.COPY_TYPE_ARENA) {
				return cfg;
			}
		}
		return null;
	}
}
