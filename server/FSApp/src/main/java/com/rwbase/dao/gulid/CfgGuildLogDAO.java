package com.rwbase.dao.gulid;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgGuildLogDAO extends CfgCsvDao<GuildLogCfg> {
	private static CfgGuildLogDAO instance = new CfgGuildLogDAO();
	private CfgGuildLogDAO() {
		
	}
	
	public static CfgGuildLogDAO getInstance(){
		return instance;
	}
	
	public Map<String, GuildLogCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guild/GuildLog.csv",GuildLogCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public GuildLogCfg getRankingCf(int id){
		return (GuildLogCfg)getCfgById(String.valueOf(id));
	}
}
