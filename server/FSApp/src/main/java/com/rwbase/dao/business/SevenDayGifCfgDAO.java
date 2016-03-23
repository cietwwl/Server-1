package com.rwbase.dao.business;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.arena.ArenaUpPrizeCfgDAO;

public class SevenDayGifCfgDAO extends CfgCsvDao<SevenDayGifCfg> {
	public static SevenDayGifCfgDAO getInstance() {
		return SpringContextUtil.getBean(SevenDayGifCfgDAO.class);
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
