package com.rwbase.dao.mainmsg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;


public class CfgPmdDAO extends CfgCsvDao<PmdCfg> {
	private static CfgPmdDAO instance = new CfgPmdDAO();
	private CfgPmdDAO() {
		
	}
	
	public static CfgPmdDAO getInstance(){
		return instance;
	}
	
	public Map<String, PmdCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("mainmsg/PmdCfg.csv",PmdCfg.class);
		return cfgCacheMap;
	}
	
	/**根据子类ID获取相应数据*/
	public PmdCfg getCfg(int id){
		return (PmdCfg)getCfgById(String.valueOf(id));
	}
}
