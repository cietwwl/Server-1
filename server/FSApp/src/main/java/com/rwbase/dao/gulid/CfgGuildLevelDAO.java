package com.rwbase.dao.gulid;

import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgGuildLevelDAO extends CfgCsvDao<GuildLevelCfg> {
	private static CfgGuildLevelDAO instance = new CfgGuildLevelDAO();
	private CfgGuildLevelDAO() {
		
	}
	
	public static CfgGuildLevelDAO getInstance(){
		return instance;
	}
	
	public Map<String, GuildLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guild/GuildLevel.csv",GuildLevelCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public GuildLevelCfg getRankingCf(int id){
		return (GuildLevelCfg)getCfgById(String.valueOf(id));
	}
	
	
    public int getUpNum(int funId,int level)
    {
    	List<GuildLevelCfg> list=getAllCfg();
    
    	
        for(GuildLevelCfg cfg : list)
        {
            if (funId == cfg.functionID && (level + 1) == cfg.level)
            {
                return cfg.num;
            }
        }

        return 0;

    }
}
