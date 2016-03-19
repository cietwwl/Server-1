package com.rwbase.dao.gamble.pojo.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwproto.GambleServiceProtos.EGambleType;

public class GambleCfgDAO extends CfgCsvDao<GambleCfg>
{
	private static GambleCfgDAO m_instance = new GambleCfgDAO();
	public static GambleCfgDAO getInstance(){
		if(m_instance == null) {
			m_instance = new GambleCfgDAO();
		}
		return m_instance;
	}
	
	public GambleCfg getGambleCfg(EGambleType gambleType){
		return (GambleCfg)getCfgById(String.valueOf(gambleType.getNumber()));
	}

	@Override
	public Map<String, GambleCfg> initJsonCfg() 
	{
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/gamble.csv",GambleCfg.class);
		return cfgCacheMap;
	}
}
