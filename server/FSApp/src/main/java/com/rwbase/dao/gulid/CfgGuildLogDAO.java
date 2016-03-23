package com.rwbase.dao.gulid;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgGuildLogDAO extends CfgCsvDao<GuildLogCfg> {
	public static CfgGuildLogDAO getInstance() {
		return SpringContextUtil.getBean(CfgGuildLogDAO.class);
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
