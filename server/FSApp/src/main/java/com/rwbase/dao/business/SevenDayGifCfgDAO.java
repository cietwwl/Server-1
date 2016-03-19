package com.rwbase.dao.business;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class SevenDayGifCfgDAO extends CfgCsvDao<SevenDayGifCfg> {
	private static SevenDayGifCfgDAO instance = new SevenDayGifCfgDAO();
	private SevenDayGifCfgDAO() {
		
	}
	
	public static SevenDayGifCfgDAO getInstance(){
		return instance;
	}
	
	public Map<String, SevenDayGifCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("mainmsg/DailyGifCfg.csv",SevenDayGifCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public SevenDayGifCfg getIdCf(int id){
		return (SevenDayGifCfg)getCfgById(String.valueOf(id));
	}
	
	
    public SevenDayGifCfg getCfg(int num)
    {
    	if(cfgCacheMap==null)
    	{
    		initJsonCfg();
    	}
        for(Object obj : cfgCacheMap.values())
        {
        	SevenDayGifCfg cfg=(SevenDayGifCfg) obj;
            if (cfg.id == num)
            {
                return cfg;
            }
        }

        return null;

    }
}
