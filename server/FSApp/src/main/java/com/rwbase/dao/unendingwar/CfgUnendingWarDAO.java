package com.rwbase.dao.unendingwar;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class CfgUnendingWarDAO extends CfgCsvDao<CfgUnendingWar> {
	private static CfgUnendingWarDAO instance = new CfgUnendingWarDAO();
	private CfgUnendingWarDAO() {
		
	}
	
	public static CfgUnendingWarDAO getInstance(){
		return instance;
	}
	
	public Map<String, CfgUnendingWar> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("UnendingWar/UnendingWarCfg.csv",CfgUnendingWar.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public CfgUnendingWar getRankingCf(int id){
		return (CfgUnendingWar)getCfgById(String.valueOf(id));
	}
	
	
    public CfgUnendingWar getCfg(int copyId,int num)
    {
    	if(cfgCacheMap==null)
    	{
    		initJsonCfg();
    	}
        for(Object obj : cfgCacheMap.values())
        {
        	CfgUnendingWar cfg=(CfgUnendingWar) obj;
            if (cfg.item1 == copyId && cfg.num == num)
            {
                return cfg;
            }
        }

        return null;

    }
}
