package com.rwbase.dao.gulid;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgGuildIconDAO extends CfgCsvDao<GuildIconCfg> {
	private static CfgGuildIconDAO instance = new CfgGuildIconDAO();
	private CfgGuildIconDAO() {
		
	}
	
	public static CfgGuildIconDAO getInstance(){
		return instance;
	}
	
	public Map<String, GuildIconCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guild/GuildIconCfg.csv",GuildIconCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public GuildIconCfg getRankingCf(int id){
		return (GuildIconCfg)getCfgById(String.valueOf(id));
	}
}
