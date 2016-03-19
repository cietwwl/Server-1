package com.rwbase.dao.publicdata;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.publicdata.pojo.PublicDataCfg;

public class PublicDataCfgDAO extends CfgCsvDao<PublicDataCfg> {

	private static PublicDataCfgDAO instance = new PublicDataCfgDAO();
	private PublicDataCfgDAO() {}
	public static PublicDataCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, PublicDataCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("PublicData/publicdata.csv", PublicDataCfg.class);
		return cfgCacheMap;
	}

	public int getPublicDataValueById(int id)
	{
		PublicDataCfg cfg = (PublicDataCfg) getCfgById(String.valueOf(id));
		return cfg.getValue();
	}
	
	public String getPublicDataStringValueById(int id)
	{
		PublicDataCfg cfg = (PublicDataCfg) getCfgById(String.valueOf(id));
		return cfg.getStrValue();
	}
}
