package com.rwbase.dao.unendingwar;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class UnendingWarInfoCfgDAO extends CfgCsvDao<UnendingWarInfoCfg> {
	private static UnendingWarInfoCfgDAO instance = new UnendingWarInfoCfgDAO();
	private UnendingWarInfoCfgDAO() {
		
	}
	
	public static UnendingWarInfoCfgDAO getInstance(){
		return instance;
	}
	
	public Map<String, UnendingWarInfoCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("UnendingWar/UnendingWarInfoCfg.csv",UnendingWarInfoCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public UnendingWarInfoCfg getRankingCf(int id){
		return (UnendingWarInfoCfg)getCfgById(String.valueOf(id));
	}
	
	
    public UnendingWarInfoCfg getCfg(int copyId)
    {
    	if(cfgCacheMap==null)
    	{
    		initJsonCfg();
    	}
        for(Object obj : cfgCacheMap.values())
        {
        	UnendingWarInfoCfg cfg=(UnendingWarInfoCfg) obj;
            if (cfg.item1 == copyId)
            {
                return cfg;
            }
        }

        return null;

    }
}
