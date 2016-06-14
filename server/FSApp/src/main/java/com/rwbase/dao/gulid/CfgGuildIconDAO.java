package com.rwbase.dao.gulid;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgGuildIconDAO extends CfgCsvDao<GuildIconCfg> {
	public static CfgGuildIconDAO getInstance() {
		return SpringContextUtil.getBean(CfgGuildIconDAO.class);
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
