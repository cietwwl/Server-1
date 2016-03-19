package com.rwbase.dao.gulid;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgGuildDAO extends CfgCsvDao<GuildCfg> {
	private static CfgGuildDAO instance = new CfgGuildDAO();
	private CfgGuildDAO() {
		
	}
	
	public static CfgGuildDAO getInstance(){
		return instance;
	}
	
	public Map<String, GuildCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guild/GuildCfg.csv",GuildCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public GuildCfg getRankingCf(int id){
		return (GuildCfg)getCfgById(String.valueOf(id));
	}
}
