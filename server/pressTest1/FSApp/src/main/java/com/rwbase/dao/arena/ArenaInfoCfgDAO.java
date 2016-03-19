package com.rwbase.dao.arena;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.arena.pojo.ArenaInfoCfg;
import com.rwbase.dao.copypve.CopyType;

public class ArenaInfoCfgDAO extends CfgCsvDao<ArenaInfoCfg> {

	private static ArenaInfoCfgDAO instance;
	private ArenaInfoCfgDAO() {}
	
	public static ArenaInfoCfgDAO getInstance()
	{
		if(instance == null){
			instance = new ArenaInfoCfgDAO();
		}
		return instance;
	}
	
	@Override
	public Map<String, ArenaInfoCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("arena/arenaInfo.csv", ArenaInfoCfg.class);
		return cfgCacheMap;
	}
	
	
	public ArenaInfoCfg getArenaInfo()
	{
		List<ArenaInfoCfg> list = getAllCfg();
		for(ArenaInfoCfg cfg : list){
			if(cfg.getCopyType() == CopyType.COPY_TYPE_ARENA){
				return cfg;
			}
		}
		return null;
	}
	
	public ArenaInfoCfg getPeakArenaInfo()
	{
		List<ArenaInfoCfg> list = getAllCfg();
		for(ArenaInfoCfg cfg : list){
			if(cfg.getCopyType() == CopyType.COPY_TYPE_PEAK_ARENA){
				return cfg;
			}
		}
		return null;
	}

}
