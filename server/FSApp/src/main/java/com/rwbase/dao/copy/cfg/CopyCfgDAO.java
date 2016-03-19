package com.rwbase.dao.copy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.JsonCfgTransfer;
import com.rwbase.common.config.CfgCsvHelper;

public class CopyCfgDAO extends CfgCsvDao<CopyCfg>{

	private static CopyCfgDAO instance = new CopyCfgDAO();
	private CopyCfgDAO() {}
	public static CopyCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, CopyCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/copy.csv",CopyCfg.class);
		return cfgCacheMap;
	}
	public CopyCfg getCfg(int id){
		return (CopyCfg)getCfgById(String.valueOf(id));
	}
}
