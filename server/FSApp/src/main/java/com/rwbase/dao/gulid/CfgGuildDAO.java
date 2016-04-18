package com.rwbase.dao.gulid;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgGuildDAO extends CfgCsvDao<GuildCfg> {
	public static CfgGuildDAO getInstance() {
		return SpringContextUtil.getBean(CfgGuildDAO.class);
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
